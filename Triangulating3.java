import java.util.LinkedList;
import java.util.ArrayList;
import java.util.TreeSet; 
import java.util.Iterator;
import java.time.ZonedDateTime;

// Crashes on my machine with 191k of the 17-gon triangulations found

class Triangulating3{

    public static int int_modulo(int a, int b){
        return (int)((a % b) + b) % b;
    }

    // Figure out if one way of eliminating duplicates here is more efficient
    public static TreeSet<Triangulation> triangulate(int n, TreeSet<Triangulation> previous_triangulations){

        System.out.println(previous_triangulations.size());

        Iterator<Triangulation> iter;
        TreeSet<Triangulation> new_triangulations = new TreeSet<Triangulation>();
        Triangulation new_triangulation; 

        // Eliminate duplicates based on whether they have ears we've already covered? 
        for(int i=1; i<n-1; i++){
            iter = previous_triangulations.iterator();
            for(int j=0; j<previous_triangulations.size(); j++){
                new_triangulation = iter.next().add_ear(i);
                new_triangulations.add(new_triangulation);
                if((new_triangulations.size() % 1000) == 0){
                    System.out.println(new_triangulations.size());
                }
            }        
        }

        return new_triangulations;
    }

    public static void main(String[] args){

        Triangulating3 t = new Triangulating3();
        t.run();
    }

    public void run(){

        ArrayList<ArrayList<Integer>> vertices3 = new ArrayList<ArrayList<Integer>>();
        vertices3.add(new ArrayList<Integer>());
        vertices3.add(new ArrayList<Integer>());
        vertices3.add(new ArrayList<Integer>());
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

        ArrayList<ArrayList<Integer>> vertices; 
        int size;

        public Triangulation(ArrayList<ArrayList<Integer>> _vertices){
            vertices = _vertices;
            size = vertices.size();
        }

        public int size(){
            return size;
        }

        // Returns a copy with an ear added before vertex n
        // n between 1 and size
        public Triangulation add_ear(int n){

            ArrayList<ArrayList<Integer>> new_vertices = new ArrayList<ArrayList<Integer>>(size + 1);
            ArrayList<Integer> new_connections;
            ArrayList<Integer> old_connections; 

            if(n == size){
                // Copy zero vertex, adding connection to vertex n-1
                new_connections = new ArrayList<Integer>();
                old_connections = vertices.get(0);
                for(int i=0; i<old_connections.size(); i++){
                    new_connections.add(old_connections.get(i));
                }
                new_connections.add(n-1);
                new_vertices.add(new_connections);

                // Copy nodes up to last
                for (int i=1; i<size-1; i++){
                    new_vertices.add(vertices.get(i));
                }

                // Copy last node, adding connection to vertex 0
                new_connections = new ArrayList<Integer>();
                old_connections = vertices.get(size-1);
                new_connections.add(0);
                for(int i=0; i<old_connections.size(); i++){
                    new_connections.add(old_connections.get(i));
                }
                new_vertices.add(new_connections);

                // Add new node
                new_vertices.add(new ArrayList<Integer>());
                return new Triangulation(new_vertices);

            }


            // Copy nodes up to inserted vertex, changing labels on connections after inserted vertex
            for(int i=0; i<(n-1); i++){
                new_connections = new ArrayList<Integer>();
                old_connections = vertices.get(i);
                for(int j=0; j<old_connections.size(); j++){
                    if (old_connections.get(j) < n){
                        new_connections.add(old_connections.get(j));
                    } else { 
                        new_connections.add(old_connections.get(j) + 1);
                    }
                }
                new_vertices.add(new_connections);
            }

            // Handle node directly before inserted vertex; needs connection added to node now labeled n+1
            new_connections = new ArrayList<Integer>();
            old_connections = vertices.get(n-1);
            int k = 0;
            for(; k<old_connections.size() && old_connections.get(k) < n; k++){
                new_connections.add(old_connections.get(k));
            }
            new_connections.add(n+1);
            for(; k < old_connections.size(); k++){
                new_connections.add(old_connections.get(k) + 1);
            }

            new_vertices.add(new_connections);

            // add new node
            new_connections = new ArrayList<Integer>();
            new_vertices.add(new_connections);

            // copy node after new node, adding new connection
            new_connections = new ArrayList<Integer>();
            old_connections = vertices.get(n);
            k = 0;
            for(; k < old_connections.size() && old_connections.get(k) < n; k++){
                new_connections.add(old_connections.get(k));
            }
            new_connections.add(n-1);
            for(; k < old_connections.size(); k++){
                new_connections.add(old_connections.get(k) + 1);
            }
            new_vertices.add(new_connections);

            // copy nodes after new node
            for(int i=n+1; i<size; i++){
                new_connections = new ArrayList<Integer>();
                old_connections = vertices.get(i);
                for(int j=0; j<old_connections.size(); j++){
                    if (old_connections.get(j) < n){
                        new_connections.add(old_connections.get(j));
                    } else { 
                        new_connections.add(old_connections.get(j) + 1);
                    }
                }
                new_vertices.add(new_connections);                
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
                if (this.vertices.get(i).size() != other.vertices.get(i).size()){
                    if (this.vertices.get(i).size() < other.vertices.get(i).size()){
                        return -1;
                    }
                    return 1;
                }

                ArrayList<Integer> this_curr_list = this.vertices.get(i);
                ArrayList<Integer> other_curr_list = other.vertices.get(i);

                if (this_curr_list.size() > 0){
                    for(int j=0; j<this_curr_list.size(); j++){
                        if (this_curr_list.get(j) != other_curr_list.get(j)){
                            if (this_curr_list.get(j) < other_curr_list.get(j)){
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
                if (this.vertices.get(i).size() != other.vertices.get(i).size()){
                    return false; 
                }

                ArrayList<Integer> this_curr_list = this.vertices.get(i);
                ArrayList<Integer> other_curr_list = other.vertices.get(i);

                if (this_curr_list.size() > 0){
                    for(int j=0; j<this_curr_list.size(); j++){
                        if (this_curr_list.get(j) != other_curr_list.get(j)){
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
                if (vertices.get(i).size() > 0){
                    ArrayList<Integer> curr_list = vertices.get(i);
                    for(int j=0; j<curr_list.size(); j++){
                        rtn = rtn + curr_list.get(j) + ",";
                    }
                }
                rtn = rtn + "]\n";
            }
            return rtn; 
        }

    }

}