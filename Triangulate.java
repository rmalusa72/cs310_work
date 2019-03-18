import java.util.TreeSet; 
import java.util.Iterator;

class Triangulate{

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
            Triangulate t = new Triangulate(n, print);
        }
    }

    // Starting with the single trivial triangulation of the triangle, build up the set of triangulations for each n-gon
    // up to the goal, then print size and (if specified) every triangulation in the set 
    public Triangulate(int n, boolean print){
        byte[][] vertices3 = new byte[3][];
        vertices3[0] = new byte[0];
        vertices3[1] = new byte[0];
        vertices3[2] = new byte[0];
        Triangulation triangle = new Triangulation(vertices3);

        TreeSet<Triangulation> ts = new TreeSet<Triangulation>();
        ts.add(triangle);

        for(int i=4; i<=n; i++){
            ts = triangulate(i, ts);
        }
        System.out.println(ts.size());
        if(print){
            Iterator<Triangulation> iter = ts.iterator();
            while(iter.hasNext()){
                System.out.println(iter.next());
            }
        }
    }

    // Return a mod b (Java % operator sometimes returns negative numbers; this does not)
    public static byte byte_modulo(byte a, byte b){
        return (byte)(((a % b) + b) % b);
    }

    // Given n and the set of triangulations for an n-1 gon, produce and return the set of triangulations for the n-gon
    public static TreeSet<Triangulation> triangulate(int n, TreeSet<Triangulation> previous_triangulations){

        Iterator<Triangulation> iter;
        TreeSet<Triangulation> new_triangulations = new TreeSet<Triangulation>();
        Triangulation new_triangulation; 
        Triangulation old_triangulation;

        // For each triangulation of the n-1 gon, insert an ear before every vertex but the last
        // This will produce some duplicates, so the triangulations are stored in a treeset for fast adding
        // Triangulations are removed from previous_triangulations as we go to save space
        iter = previous_triangulations.iterator();
        while(previous_triangulations.size() > 0){
            old_triangulation = iter.next();
            for(int i=1; i<n-1; i++){
                new_triangulation = old_triangulation.add_ear(i);
                new_triangulations.add(new_triangulation);
            }
            iter.remove();
        }

        return new_triangulations;
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