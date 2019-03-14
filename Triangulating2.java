import java.util.LinkedList;
import java.util.ArrayList;
import java.util.TreeSet; 
import java.util.Iterator;
import java.time.ZonedDateTime;

// What if we replaced list of ints with list of many booleans? Worse copying, but easier rotation

class Triangulating2{

    public static int int_modulo(int a, int b){
        return (int)((a % b) + b) % b;
    }

    // Changes the head of a ciphered cyclic list of integers to make it be in order
    // Note: designed for lists that have been rotated 1 step, so only checks out first and second nodes! 
    public static void recenter_list(CyclicList<Integer> clist){
        CNode<Integer> head = clist.head();
        if (head.element() > head.prev().element()){
            clist.setHead(head.prev());
        }
    }

    // Figure out if one way of eliminating duplicates here is more efficient
    public static ArrayList<Triangulation> triangulate(int n, ArrayList<Triangulation> previous_triangulations){

        // System.out.println("Triangulating");

        TreeSet<Triangulation> new_triangulations = new TreeSet<Triangulation>();

        for (int i=0; i<previous_triangulations.size(); i++){
            previous_triangulations.get(i).add_ear();
            new_triangulations.add(previous_triangulations.get(i));
        }

        for (int i=0; i<previous_triangulations.size(); i++){
            Triangulation t = previous_triangulations.get(i);
            Triangulation initial_t = t; 

            for(int j=1; j<n; j++){
                t = t.rotate();
                if (t.equals(initial_t)){
                    break;
                }
                if (!(new_triangulations.add(t))){
                    break;
                }
            }
        }

        ArrayList<Triangulation> new_triangulations_list = new ArrayList<Triangulation>();

        Iterator<Triangulation> iter = new_triangulations.iterator();
        for (int i=0; i<new_triangulations.size(); i++){
            new_triangulations_list.add(iter.next());
        }

        System.gc();
        return new_triangulations_list;
    }

    public static void main(String[] args){
        Triangulating2 t = new Triangulating2();
        t.run();
    }

    public void run(){

        CyclicList<CyclicList<Integer>> vertices3 = new CyclicList<CyclicList<Integer>>();
        vertices3.add(new CyclicList<Integer>());
        vertices3.add(new CyclicList<Integer>());
        vertices3.add(new CyclicList<Integer>());
        Triangulation triangle = new Triangulation(vertices3);


        // CyclicList<CyclicList<Integer>> vertices4 = new CyclicList<CyclicList<Integer>>();
        // vertices4.add(new CyclicList<Integer>(2));
        // System.out.println(vertices4);
        // vertices4.add(new CyclicList<Integer>());
        // System.out.println(vertices4);
        // vertices4.add(new CyclicList<Integer>(0));
        // System.out.println(vertices4);
        // vertices4.add(new CyclicList<Integer>());
        // System.out.println(vertices4);
        // Triangulation t = new Triangulation(vertices4);

        // System.out.println(t);

        // CyclicList<CyclicList<Integer>> vertices4_2 = new CyclicList<CyclicList<Integer>>();
        // vertices4_2.add(new CyclicList<Integer>());
        // vertices4_2.add(new CyclicList<Integer>(3));
        // vertices4_2.add(new CyclicList<Integer>());
        // vertices4_2.add(new CyclicList<Integer>(1));
        // Triangulation t2 = new Triangulation(vertices4_2);

        // System.out.println(t2);

        // Triangulation rotated_t = t.rotate((int)1);
        // System.out.println(rotated_t);
        // System.out.println(rotated_t.equals(t2));
        // System.out.println(t.equals(t2));

        // t.add_ear();
        // System.out.println(t);
        // t = t.rotate(1);
        // System.out.println(t);
        // t = t.rotate(1);
        // System.out.println(t);
        // t = t.rotate(1);
        // System.out.println(t);
        // t = t.rotate(1);
        // System.out.println(t);
        // t = t.rotate(1);
        // System.out.println(t);

        ArrayList<Triangulation> ts = new ArrayList<Triangulation>();
        ts.add(triangle);
        for(int i=4; i<40; i++){
            ts = triangulate(i, ts);
            System.out.println(ZonedDateTime.now());
            System.out.println(ts.size());
        }


        // ArrayList<Triangulation> fours = new ArrayList<Triangulation>();
        // fours.add(t);
        // fours.add(t2);
        // ArrayList<Triangulation> fives = triangulate(5, fours);
        // System.out.println(fives);
        // System.out.println(fives.size());

        // ArrayList<Triangulation> sixes = triangulate(6, fives);
        // System.out.println(sixes.size());

        // ArrayList<Triangulation> sevens = triangulate(7, sixes);
        // System.out.println(sevens.size());

    }

    private class CyclicList<E>{
        CNode<E> head;
        int size;

        public CyclicList(E element){
            head = new CNode<E>(element);
            head.setPrev(head);
            head.setNext(head);
            size = 1; 
        }

        public CyclicList(){
            head = null;
            size = 0;
        }

        public CNode<E> head(){
            return head;
        }

        public int size(){
            return size;
        }

        // Adds a new element directly before the head node (to the end of the list)
        public void add(E element){
            if (size == 0){
                head = new CNode<E>(element);
                head.setPrev(head);
                head.setNext(head);
                size = 1;
            } else {
                CNode<E> tail = head.prev;
                tail.setNext(new CNode<E>(element));
                tail.next.setNext(head);
                tail.next.setPrev(tail);
                head.setPrev(tail.next);
                size += 1;
            }
        }

        // Adds a new element to the beginning of the list
        public void addFirst(E element){
            if (size == 0){
                head = new CNode<E>(element);
                head.setPrev(head);
                head.setNext(head);
                size = 1;
            } else {
                CNode<E> tail = head.prev;
                CNode<E> new_head = new CNode<E>(element);
                tail.setNext(new_head);
                head.setPrev(new_head);
                new_head.setPrev(tail);
                new_head.setNext(head);
                head = head.prev();
                size += 1;
            }
        }

        public void rotate(int n){
            CNode<E> curr = head; 
            for (int i=0; i<n; i++){
                curr = curr.prev;
            }
            head = curr;
        }

        public void setHead(CNode<E> _head){
            head = _head;
        }

        @Override
        public String toString(){
            String rtn = "[";
            CNode<E> curr = head;
            for (int i=0; i<size; i++){
                rtn = rtn + curr.element() + ",";
                curr = curr.next();
            }
            return rtn + "]";
        }

    }

    private class CNode<E>{
        CNode<E> prev;
        CNode<E> next;
        E element;

        public CNode(E _element){
            element = _element;
            prev = null;
            next = null;
        }

        public void setPrev(CNode<E> _prev){
            prev = _prev;
        }

        public void setNext(CNode<E> _next){
            next = _next;
        }

        public E element(){
            return element;
        }

        public CNode<E> next(){
            return next;
        }

        public CNode<E> prev(){
            return prev;
        }
    }

    private class Triangulation implements Comparable<Triangulation>{

        CyclicList<CyclicList<Integer>> vertices;
        CyclicList<Integer> connection_numbers;
        int size;

        public Triangulation(CyclicList<CyclicList<Integer>> _vertices){
            vertices = _vertices;
            size = vertices.size();
        }

        public int size(){
            return size;
        }

/*        // Rotate n steps
        public Triangulation rotate(int n){
            int[] replacement_key = new int[size];
            for(int i=0; i<size; i++){
                replacement_key[i] = (i + n) % size;
            }

            CyclicList<CyclicList<Integer>> new_vertices = new CyclicList<CyclicList<Integer>>();
            CNode<CyclicList<Integer>> curr_outer_node = vertices.head();

            for (int i=0; i<size; i++){
                if(curr_outer_node.element().size() > 0){
                    CyclicList<Integer> new_connections = new CyclicList<Integer>();
                    CyclicList<Integer> old_connections = curr_outer_node.element();
                    CNode<Integer> curr_inner_node = old_connections.head();
                    for (int j=0; j<curr_outer_node.element().size(); j++){
                        new_connections.add(replacement_key[curr_inner_node.element()]);
                        curr_inner_node = curr_inner_node.next();
                    }
                    recenter_list(new_connections);
                    new_vertices.add(new_connections);
                } else {
                    new_vertices.add(new CyclicList<Integer>());
                }
                curr_outer_node = curr_outer_node.next();
            }

            new_vertices.rotate(n);

            return new Triangulation(new_vertices);

        }*/

        // Rotate one step 
        public Triangulation rotate(){
            int[] replacement_key = new int[size];
            for(int i=0; i<size; i++){
                replacement_key[i] = (i + 1) % size;
            }

            CyclicList<CyclicList<Integer>> new_vertices = new CyclicList<CyclicList<Integer>>();
            CNode<CyclicList<Integer>> curr_outer_node = vertices.head();

            for (int i=0; i<size; i++){
                if(curr_outer_node.element().size() > 0){
                    CyclicList<Integer> new_connections = new CyclicList<Integer>();
                    CyclicList<Integer> old_connections = curr_outer_node.element();
                    CNode<Integer> curr_inner_node = old_connections.head();
                    for (int j=0; j<curr_outer_node.element().size(); j++){
                        new_connections.add(replacement_key[curr_inner_node.element()]);
                        curr_inner_node = curr_inner_node.next();
                    }
                    recenter_list(new_connections);
                    new_vertices.add(new_connections);
                } else {
                    new_vertices.add(new CyclicList<Integer>());
                }
                curr_outer_node = curr_outer_node.next();
            }

            new_vertices.rotate(1);

            return new Triangulation(new_vertices);

        }

        public void add_ear(){
            CNode<CyclicList<Integer>> head = vertices.head();
            CNode<CyclicList<Integer>> tail = vertices.head().prev();

            // System.out.println("head: " + head);
            // System.out.println("tail: " + tail);

            head.element().add(size-1);
            tail.element().addFirst(0);
            CNode<CyclicList<Integer>> new_tail = new CNode<CyclicList<Integer>>(new CyclicList<Integer>());
            head.setPrev(new_tail);
            tail.setNext(new_tail);
            new_tail.setPrev(tail);
            new_tail.setNext(head);

            size += 1;
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

            CNode<CyclicList<Integer>> this_curr = this.vertices.head();
            CNode<CyclicList<Integer>> other_curr = other.vertices.head();
            for (int i=0; i<size; i++){
                if (this_curr.element().size() != other_curr.element().size()){
                    if(this_curr.element().size() < other_curr.element().size()){
                        return -1;
                    }
                    return 1; 
                }
                if (this_curr.element().size() > 0){
                    CNode<Integer> this_inner_curr = this_curr.element().head();
                    CNode<Integer> other_inner_curr = other_curr.element().head();

                    for(int j=0; j<this_curr.element().size(); j++){
                        if (this_inner_curr.element() != other_inner_curr.element()){
                            if (this_inner_curr.element() < other_inner_curr.element()){
                                return -1;
                            }
                            return 1;
                        }
                        this_inner_curr = this_inner_curr.next();
                        other_inner_curr = other_inner_curr.next();
                    }
                }
                this_curr = this_curr.next();
                other_curr = other_curr.next();
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

            CNode<CyclicList<Integer>> this_curr = this.vertices.head();
            CNode<CyclicList<Integer>> other_curr = other.vertices.head();
            for (int i=0; i<size; i++){
                // System.out.println("Equals loop");
                if (this_curr.element().size() != other_curr.element().size()){
                    return false;
                }
                if (this_curr.element().size() > 0){
                    CNode<Integer> this_inner_curr = this_curr.element().head();
                    CNode<Integer> other_inner_curr = other_curr.element().head();
                    for (int j=0; j<this_curr.element().size(); j++){
                        if (this_inner_curr.element() != other_inner_curr.element()){
                            return false;
                        }
                        this_inner_curr = this_inner_curr.next();
                        other_inner_curr = other_inner_curr.next();
                    }

                }
                this_curr = this_curr.next();
                other_curr = other_curr.next();
            }

            return true;
        }

        @Override
        public String toString(){
            String rtn = "";
            CNode<CyclicList<Integer>> curr = vertices.head();
            for (int i=0; i<size; i++){
                rtn = rtn + i + ": [";
                if(curr.element().size() > 0){
                    CNode<Integer> inner_curr = curr.element().head();
                    for (int j=0; j<curr.element().size(); j++){
                        rtn = rtn + inner_curr.element() + ",";
                        inner_curr = inner_curr.next();
                    }
                }
                rtn = rtn + "]\n";
                curr = curr.next();
            }
            return rtn; 
        }

    }

}