import java.util.TreeSet; 
import java.util.Iterator;
import java.io.File;
import java.io.FileOutputStream;
import java.io.BufferedWriter;
import java.io.FileWriter; 
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.io.FileInputStream;

class Triangulate5{

    static BufferedWriter writer;
    static BufferedReader reader;
    static byte writer_pos;
    static byte reader_pos;
    static byte writer_buffer;
    static byte reader_buffer; 
    final byte DIVIDER = (byte) 31;

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
            Triangulate5 t = new Triangulate5(n, print);
        }
    }

    public static void initializeReader(String filename){
        try{
            reader = new BufferedReader(new FileReader(new File(filename)));
            reader_pos = 8;
            reader_buffer = 0;
        } catch (IOException e){
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }

    public static void initializeWriter(String filename){
        try{
            writer = new BufferedWriter(new FileWriter(new File(filename)));
            writer_pos = 0;
            writer_buffer = 0;
        } catch (IOException e){
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }

    // write the last five bits of this number to file
    public static void write(byte towrite){
        try{
            int two_pwr = 16; 
            boolean bit; 
            for(int i=0; i<5; i++){
                if(writer_pos == 8){
                    writer.write(writer_buffer);
                    writer_buffer = 0; 
                    writer_pos = 0; 
                } 
                bit = ((towrite & two_pwr) != 0);
                if (bit){
                    writer_buffer = (byte) (writer_buffer | (1 << (7-writer_pos)));
                }
                writer_pos += 1;
                two_pwr = two_pwr / 2;
            }
        } catch (IOException e){
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }

    public static void closeWriter(){
        try{
            if(writer_pos > 0){
                writer.write(writer_buffer);
            }
            writer.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }

    // reads the next five bits of the input stream as a byte
    public static byte read(){
        int two_pwr = 16; 
        byte rtn = 0;
        boolean bit; 
        try{
            for(int i=0; i<5; i++){
                if (reader_pos == 8){
                    reader_buffer = (byte)reader.read();
                    reader_pos = 0; 
                }
                bit = ((reader_buffer & (1 << (7-reader_pos)))!=0);
                if(bit){
                    rtn = (byte) (rtn + two_pwr);
                }
                reader_pos += 1;
                two_pwr = two_pwr / 2;
            }
        } catch (IOException e){
            System.out.println(e.getMessage());
            System.exit(1);
        }
        return rtn; 
    }

    public static void closeReader(){
        try{
            reader.close();
        } catch(IOException e){
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }


    public Triangulate5(int n, boolean print){
        
        byte[][] vertices3 = new byte[3][];
        vertices3[0] = new byte[0];
        vertices3[1] = new byte[0];
        vertices3[2] = new byte[0];
        Triangulation triangle = new Triangulation(vertices3);
        int ts_size = 1;

        initializeWriter("ts3");
        write(DIVIDER);
        for(int i=0; i<3; i++){
            write(DIVIDER);
            for(int j=0; j<triangle.vertices[i].length; j++){
                write(triangle.vertices[i][j]);
            }
            write(DIVIDER);
        }
        write(DIVIDER);
        closeWriter();

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

        initializeReader("ts" + (n-1));
        initializeWriter("ts" + n);

        int triangulations_read = 0;
        byte[][] last_triangulation_vertices = new byte[n-1][];
        boolean in_triangulation = false;
        int position_in_triangulation = 0;
        ArrayList<Byte> current_connections_list; 
        Triangulation old_triangulation; 
        Triangulation new_triangulation; 

        while(triangulations_read < last_size){
            int next = read();
            if (next == DIVIDER && !in_triangulation){
                in_triangulation = true;
                position_in_triangulation = 0;
            } else if (next == DIVIDER && in_triangulation && position_in_triangulation < n-1){
                current_connections_list = new ArrayList<Byte>();
                next = read();
                while (!(next == DIVIDER)){
                    current_connections_list.add((byte)next);
                    next = read();
                }
                last_triangulation_vertices[position_in_triangulation] = new byte[current_connections_list.size()];
                for(int i=0; i<current_connections_list.size(); i++){
                    last_triangulation_vertices[position_in_triangulation][i] = current_connections_list.get(i);
                }
                position_in_triangulation +=1; 
            } else if (next == DIVIDER && in_triangulation && (position_in_triangulation == (n-1))){
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
                        write(DIVIDER);
                        for(int k=0; k<n; k++){
                            write(DIVIDER);
                            for(int l=0; l<new_triangulation.vertices[k].length; l++){
                                write(new_triangulation.vertices[k][l]);
                            }
                            write(DIVIDER);
                        }
                        write(DIVIDER);
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

        closeReader();
        closeWriter();
        File readfile = new File("ts" + (n-1));
        readfile.delete();

        System.out.println(ZonedDateTime.now());
	    System.out.println(num_triangulations);
        return num_triangulations;
    }

    // Represents a single triangulation of an n-gon 
    // Each byte[] in vertices represents a vertex's set of connections to other vertices
    // Each byte[] also has entries kept in sorted order, so that comparisons are fast
    private class Triangulation{
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
