import java.util.LinkedList;
import java.util.ArrayList;
import java.util.TreeSet; 
import java.util.Iterator;
import java.time.ZonedDateTime;

// Crashes on my machine with 1.71 mil of the 17gon triangulations found
// Now with the tiny booleans it crashes with ..... 370k???? before the SEVENTEEN gon? 

class Triangulating5{

    public static boolean[] tinify(int n){
        boolean[] rtn = new boolean[5];
        if (n/16 == 1){
            rtn[0] = true;
            n = n-16;
        }
        if (n/8 == 1){
            rtn[1] = true;
            n = n-8;
        }
        if (n/4 == 1){
            rtn[2] = true;
            n = n-4;
        }
        if(n/2 == 1){
            rtn[3] = true;
            n = n-2;
        }
        if(n != 0){
            rtn[4] = true;
        }
        return rtn;
    }

    public static int bigify(boolean[] tiny){
        int rtn = 0;
        int two_p = 1; 
        for(int i=4; i>=0; i--){
            if (tiny[i]){
                rtn = rtn + two_p;
            }
            two_p = two_p * 2;
        }
        return rtn;
    }

    public static boolean[] increment(boolean[] tiny){

        boolean[] rtn = new boolean[tiny.length];

        int i;

        for(i=4; i>=0; i--){
            if(!tiny[i]){
                rtn[i] = true;
                break;
            } else {
                rtn[i] = false;
            }
        }

        for(i--;i>=0; i--){
            rtn[i] = tiny[i];
        }

        return rtn; 
    }

    public static int compareTiny(boolean[] a, boolean[] b){
        for(int i=0; i<5; i++){
            if (!a[i] && b[i]){
                return -1; 
            }
            if (a[i] && !b[i]){
                return 1;
            }
        }
        return 0; 
    }

    public static int int_modulo(int a, int b){
        return (int)((a % b) + b) % b;
    }

    // Figure out if one way of eliminating duplicates here is more efficient
    public static TreeSet<Triangulation> triangulate(int n, TreeSet<Triangulation> previous_triangulations){

        Iterator<Triangulation> iter = previous_triangulations.iterator();
        TreeSet<Triangulation> new_triangulations = new TreeSet<Triangulation>();
        Triangulation old_triangulation; 
        Triangulation new_triangulation; 

        while (previous_triangulations.size() > 0){
            old_triangulation = iter.next();
            for (int j=1; j<n-1; j++){
                new_triangulation = old_triangulation.add_ear(j);
                new_triangulations.add(new_triangulation);
                if((new_triangulations.size() % 1000) == 0){
                    System.out.println(new_triangulations.size());
                }
            }
            iter.remove();
        }

        return new_triangulations;
    }

    public static void main(String[] args){

        Triangulating5 t = new Triangulating5();
        t.run();
    }

    public void run(){

        boolean[][][] vertices3 = new boolean[3][][];
        vertices3[0] = new boolean[0][5];
        vertices3[1] = new boolean[0][5];
        vertices3[2] = new boolean[0][5];
        Triangulation triangle = new Triangulation(vertices3);
        TreeSet<Triangulation> ts = new TreeSet<Triangulation>();
        ts.add(triangle);


        for(int i=4; i<40; i++){
            ts = triangulate(i, ts);
            System.out.println(ZonedDateTime.now());
            System.out.println(ts.size());
        }

    }

    private class Triangulation implements Comparable<Triangulation>{

        boolean[][][] vertices; 
        int size;

        public Triangulation(boolean[][][] _vertices){
            vertices = _vertices;
            size = vertices.length;
        }

        public int size(){
            return size;
        }

        // Returns a copy with an ear added before vertex n
        // n between 1 and size
        public Triangulation add_ear(int n){

            boolean[][][] new_vertices = new boolean[size+1][][];
            boolean[][] new_connections;
            boolean[][] old_connections; 
            boolean[] tiny_n = tinify(n);

            if(n == size){
                // Copy zero vertex, adding connection to vertex n-1
                old_connections = vertices[0];
                new_connections = new boolean[old_connections.length + 1][5];
                for(int i=0; i<old_connections.length; i++){
                    new_connections[i] = old_connections[i];
                }

                new_connections[old_connections.length] = tinify(n-1);
                new_vertices[0] = new_connections;

                // Copy nodes up to last
                for (int i=1; i<size-1; i++){
                    new_vertices[i] = vertices[i];
                }

                // Copy last node, adding connection to vertex 0
                old_connections = vertices[size-1];
                new_connections = new boolean[old_connections.length + 1][5];
                new_connections[0] = tinify(0);
                for(int i=0; i<old_connections.length; i++){
                    new_connections[i+1] = old_connections[i];
                }
                new_vertices[size-1] = new_connections;

                // Add new node
                new_vertices[size] = new boolean[0][5];
                return new Triangulation(new_vertices);

            }

            // Copy nodes up to inserted vertex, changing labels on connections after inserted vertex
            for(int i=0; i<(n-1); i++){
                old_connections = vertices[i];
                new_connections = new boolean[old_connections.length][5];
                for(int j=0; j<old_connections.length; j++){
                    if (compareTiny(old_connections[j], tiny_n) < 0){
                        new_connections[j] = old_connections[j];
                    } else { 
                        new_connections[j] = increment(old_connections[j]);
                    }
                }
                new_vertices[i] = new_connections;
            }

            // Handle node directly before inserted vertex; needs connection added to node now labeled n+1
            old_connections = vertices[n-1];
            new_connections = new boolean[old_connections.length + 1][5];
            int k = 0;
            for(; k<old_connections.length && compareTiny(old_connections[k], tiny_n) < 0; k++){
                new_connections[k] = old_connections[k];
            }
            new_connections[k] = tinify(n+1);
            for(; k < old_connections.length; k++){
                new_connections[k+1] = increment(old_connections[k]);
            }
            new_vertices[n-1] = new_connections;

            // add new node
            new_vertices[n] = new boolean[0][5];

            // copy node after new node, adding new connection
            old_connections = vertices[n];
            new_connections = new boolean[old_connections.length + 1][5];
            k = 0;
            for(; k < old_connections.length && compareTiny(old_connections[k], tiny_n) < 0; k++){
                new_connections[k] = old_connections[k];
            }
            new_connections[k] = tinify(n-1);
            for(; k < old_connections.length; k++){
                new_connections[k+1] = increment(old_connections[k]);
            }
            new_vertices[n+1] = new_connections;

            // copy nodes after new node
            for(int i=n+1; i<size; i++){
                old_connections = vertices[i];
                new_connections = new boolean[old_connections.length][5];
                for(int j=0; j<old_connections.length; j++){
                    if (compareTiny(old_connections[j], tiny_n) < 0){
                        new_connections[j] = old_connections[j];
                    } else { 
                        new_connections[j] = increment(old_connections[j]);
                    }
                }
                new_vertices[i+1] = new_connections;             
            }

            return new Triangulation(new_vertices);
        }

        @Override
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

                boolean[][] this_curr_list = this.vertices[i];
                boolean[][] other_curr_list = other.vertices[i];

                if (this_curr_list.length > 0){
                    for(int j=0; j<this_curr_list.length; j++){
                        int comparison = compareTiny(this_curr_list[j], other_curr_list[j]);
                        if (comparison != 0){
                            return comparison; 
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

                boolean[][] this_curr_list = this.vertices[i];
                boolean[][] other_curr_list = other.vertices[i];

                if (this_curr_list.length > 0){
                    for(int j=0; j<this_curr_list.length; j++){
                        int comparison = compareTiny(this_curr_list[j], other_curr_list[j]);
                        if (comparison != 0){
                            return false; 
                        }
                    }
                }
            }

            return true; 
        }

        @Override
        public String toString(){
            String rtn = "";
            for (int i=0; i<size; i++){
                rtn = rtn + i + ": [";
                if (vertices[i].length > 0){
                    boolean[][] curr_list = vertices[i];
                    if (curr_list.length > 0){
                        for(int j=0; j<curr_list.length; j++){
                            // for (int k=0; k<curr_list[j].length; k++){
                            //     rtn = rtn + curr_list[j][k];
                            // }
                            rtn = rtn + bigify(curr_list[j]) + ",";
                        }
                    }
                }
                rtn = rtn + "]\n";
            }
            return rtn; 
        }

    }

}