import java.util.TreeSet; 
import java.util.Iterator;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.io.FileNotFoundException;
import java.io.IOException;

class Triangulate3{

    public static void main(String[] args){
        if (args.length == 0){
            System.out.println("Please specify the value of n");
            System.exit(1);
        }
        if (args.length >= 1){
            int n = Integer.parseInt(args[0]);
            if (n < 3){
                System.out.println("Please specify an n > 3");
                System.exit(1);
            }
            boolean print=false;
            if (args.length >=2){
                if (args[1].equals("-p")){
                    print = true;
                }
            }
            Triangulate3 t = new Triangulate3(n, print);
        }
    }

    public Triangulate3(int n, boolean print){
        byte[][] vertices3 = new byte[3][];
        vertices3[0] = new byte[0];
        vertices3[1] = new byte[0];
        vertices3[2] = new byte[0];
        Triangulation triangle = new Triangulation(vertices3);
        int ts_size = 1;

        try{
            String writefilepath = "ts3";
            File writefile = new File(writefilepath);
            FileOutputStream writer = new FileOutputStream(writefile);

            writer.write(255);
            for(int i=0; i<3; i++){
                writer.write(255);
                for(int j=0; j<triangle.vertices[i].length; j++){
                    writer.write(triangle.vertices[i][j]);
                }
                writer.write(255);
                writer.flush();
            }
            writer.write(255);
            writer.close();
        } catch (IOException e){
            System.out.println(e.getMessage());
            System.exit(1);
        }

        for(int i=4; i<n; i++){
            ts_size = triangulate(i, ts_size, false);
        }
        ts_size = triangulate(n, ts_size, print);
        System.out.println(ts_size);
    }

    // Return a mod b (Java % operator sometimes returns negative numbers; this does not)
    public static byte byte_modulo(byte a, byte b){
        return (byte)(((a % b) + b) % b);
    }

    // Given n, read the triangulations of the n-1-gon from file, build up the triangulations of the n-gon, and write THEM to file; return size 
    public int triangulate(int n, int last_size, boolean print){

        int num_triangulations = 0; 
        String readfilepath = "ts" + (n-1);
        File readfile = new File(readfilepath);
        FileInputStream reader;

        try{
            reader = new FileInputStream(readfile);
            int triangulations_read = 0;
            byte[][] last_triangulation_vertices = new byte[n-1][];
            boolean in_triangulation = false;
            int position_in_triangulation = 0;
            ArrayList<Byte> current_connections_list; 
            Triangulation old_triangulation; 
            Triangulation new_triangulation; 

            String writefilepath = "ts" + n;
            File writefile = new File(writefilepath);
            FileOutputStream writer = new FileOutputStream(writefile);
            Triangulation write_triangulation; 

            while(triangulations_read < last_size){
                int next = reader.read();
                if (next == 255 && !in_triangulation){
                    in_triangulation = true;
                    position_in_triangulation = 0;
                } else if (next == 255 && in_triangulation && position_in_triangulation < n-1){
                    current_connections_list = new ArrayList<Byte>();
                    next = reader.read();
                    while (!(next == 255)){
                        current_connections_list.add((byte)next);
                        next = reader.read();
                    }
                    last_triangulation_vertices[position_in_triangulation] = new byte[current_connections_list.size()];
                    for(int i=0; i<current_connections_list.size(); i++){
                        last_triangulation_vertices[position_in_triangulation][i] = current_connections_list.get(i);
                    }
                    position_in_triangulation +=1; 
                } else if (next == 255 && in_triangulation && (position_in_triangulation == (n-1))){
                    in_triangulation = false;
                    old_triangulation = new Triangulation(last_triangulation_vertices);

                    for(int i=1; i<n-1; i++){
                        new_triangulation = old_triangulation.add_ear(i);
                        boolean duplicate = false;
                        for(int j = 1; j<i-1; j++){
                            if (new_triangulation.is_ear(j)){
                                duplicate = true;
                                break;
                            }
                        }
                        if (!duplicate){
                            writer.write(255);
                            for(int k=0; k<n; k++){
                                writer.write(255);
                                for(int l=0; l<new_triangulation.vertices[k].length; l++){
                                    writer.write(new_triangulation.vertices[k][l]);
                                }
                                writer.write(255);
                            }
                            writer.write(255);
                            num_triangulations = num_triangulations + 1; 
			    if(num_triangulations % 100000 == 0){
				System.out.println(num_triangulations);
			    }
                            // if print, print this triangulation
                        }

                    }

                    last_triangulation_vertices = new byte[n-1][];
                    triangulations_read += 1; 
                }
            }

            reader.close();
            writer.flush();
            writer.close();

        } catch(IOException e){
            System.out.println(e.getMessage());
            System.exit(1);
        }
	System.out.println(num_triangulations);
        return num_triangulations;
    }

    // Represents a single triangulation of an n-gon 
    // Each byte[] in vertices represents a vertex's set of connections to other vertices
    // Each byte[] also has entries kept in sorted order, so that comparisons are fast
    private class Triangulation implements Comparable<Triangulation>{
        byte[][] vertices; 
        int size;

        public Triangulation(byte[][] _vertices){
            vertices = _vertices;
            size = vertices.length;
        }

        public int size(){
            return size;
        }

        // Returns true if the nth vertex is an ear (has no connections)
        public boolean is_ear(int n){
            if (vertices[n].length == 0){
                return true;
            }
            return false;
        }

        // Returns a copy with an ear added before vertex n
        // n between 1 and size
        public Triangulation add_ear(int n){

            byte[][] new_vertices = new byte[size+1][];
            byte[] new_connections;
            byte[] old_connections; 

            if(n == size){
                // Copy zero vertex, adding connection to vertex n-1
                old_connections = vertices[0];
                new_connections = new byte[old_connections.length + 1];
                for(int i=0; i<old_connections.length; i++){
                    new_connections[i] = old_connections[i];
                }

                new_connections[old_connections.length] = (byte)(n-1);
                new_vertices[0] = new_connections;

                // Copy nodes up to last
                for (int i=1; i<size-1; i++){
                    new_vertices[i] = vertices[i];
                }

                // Copy last node, adding connection to vertex 0
                old_connections = vertices[size-1];
                new_connections = new byte[old_connections.length + 1];
                new_connections[0] = 0;
                for(int i=0; i<old_connections.length; i++){
                    new_connections[i+1] = old_connections[i];
                }
                new_vertices[size-1] = new_connections;

                // Add new node
                new_vertices[size] = new byte[0];
                return new Triangulation(new_vertices);

            }


            // Copy nodes up to inserted vertex, changing labels on connections after inserted vertex
            for(int i=0; i<(n-1); i++){
                old_connections = vertices[i];
                new_connections = new byte[old_connections.length];
                for(int j=0; j<old_connections.length; j++){
                    if (old_connections[j] < n){
                        new_connections[j] = old_connections[j];
                    } else { 
                        new_connections[j] = (byte)(old_connections[j] + 1);
                    }
                }
                new_vertices[i] = new_connections;
            }

            // Handle node directly before inserted vertex; needs connection added to node now labeled n+1
            old_connections = vertices[n-1];
            new_connections = new byte[old_connections.length + 1];
            int k = 0;
            for(; k<old_connections.length && old_connections[k] < n; k++){
                new_connections[k] = old_connections[k];
            }
            new_connections[k] = (byte)(n+1);
            for(; k < old_connections.length; k++){
                new_connections[k+1] = (byte)(old_connections[k] + 1);
            }
            new_vertices[n-1] = new_connections;

            // add new node
            new_vertices[n] = new byte[0];

            // copy node after new node, adding new connection
            old_connections = vertices[n];
            new_connections = new byte[old_connections.length + 1];
            k = 0;
            for(; k < old_connections.length && old_connections[k] < n; k++){
                new_connections[k] = old_connections[k];
            }
            new_connections[k] = (byte)(n-1);
            for(; k < old_connections.length; k++){
                new_connections[k+1] = (byte)(old_connections[k] + 1);
            }
            new_vertices[n+1] = new_connections;

            // copy nodes after new node
            for(int i=n+1; i<size; i++){
                old_connections = vertices[i];
                new_connections = new byte[old_connections.length];
                for(int j=0; j<old_connections.length; j++){
                    if (old_connections[j] < n){
                        new_connections[j] = old_connections[j];
                    } else { 
                        new_connections[j] = (byte)(old_connections[j] + 1);
                    }
                }
                new_vertices[i+1] = new_connections;             
            }

            return new Triangulation(new_vertices);
        }

        // Orders triangulations by 1) number of connections at each vertex and
        // 2) if there are the same amount, lexicographic ordering of connection labels
        public int compareTo(Triangulation other){
            if (other.equals(null)){
                throw new NullPointerException();
            }

            if (other.size != this.size){
                if (this.size < other.size){
                    return -1;
                }
                return 1; 
            }

            for(int i=0; i<size; i++){
                if (this.vertices[i].length != other.vertices[i].length){
                    if (this.vertices[i].length < other.vertices[i].length){
                        return -1;
                    }
                    return 1;
                }

                byte[] this_curr_list = this.vertices[i];
                byte[] other_curr_list = other.vertices[i];

                if (this_curr_list.length > 0){
                    for(int j=0; j<this_curr_list.length; j++){
                        if (this_curr_list[j] != other_curr_list[j]){
                            if (this_curr_list[j] < other_curr_list[j]){
                                return -1;
                            }
                            return 1;
                        }
                    }
                }
            }

            return 0; 
        }

        @Override 
        public boolean equals(Object o){
            if (!(o instanceof Triangulation)){
                return false;
            }

            Triangulation other = (Triangulation) o;
            if (other.size != this.size){
                return false; 
            }

            for(int i=0; i<size; i++){
                if (this.vertices[i].length != other.vertices[i].length){
                    return false; 
                }

                byte[] this_curr_list = this.vertices[i];
                byte[] other_curr_list = other.vertices[i];

                if (this_curr_list.length > 0){
                    for(int j=0; j<this_curr_list.length; j++){
                        if (this_curr_list[j] != other_curr_list[j]){
                            return false; 
                        }
                    }
                }
            }

            return true; 
        }

        @Override
        public String toString(){
            // Every diagonal is counted twice, so add diagonals to a treeset for fast adding
            // without duplicates, and then iterate through that treeset to print

            TreeSet<String> diagonals = new TreeSet<String>();

            for(int i=0; i<size; i++){
                for(int j=0; j<vertices[i].length; j++){
                    if(i < vertices[i][j]){
                        diagonals.add(i + "," + vertices[i][j]);
                    } else { 
                        diagonals.add(vertices[i][j] + "," + i);
                    }
                }
            }

            String rtn = "[";
            Iterator<String> diag_iter = diagonals.iterator();
            while (diag_iter.hasNext()){
                rtn = rtn + "(" + diag_iter.next() + ")";
            }
            rtn = rtn + "]";
            return rtn; 
        }

    }

}
