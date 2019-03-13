import java.util.LinkedList;
import java.util.ListIterator;
import java.util.ArrayList;

// What if we replaced list of ints with list of many booleans? 

class Triangulating{

    public static void main(String[] args){
        Triangulating t = new Triangulating();
        t.run();
    }

    public static int int_modulo(int a, int b){
        return (int)((a % b) + b) % b;
    }

    public static void recenter_list(CyclicList<Integer> clist){
        CNode<Integer> curr = clist.head();
        while(curr.element() < curr.next().element()){
            curr = curr.next();
        }
        clist.setHead(curr.next());
    }

    public void run(){

        CyclicList<CyclicList<Integer>> vertices4 = new CyclicList<CyclicList<Integer>>();
        vertices4.add(new CyclicList<Integer>(2));
        System.out.println(vertices4);
        vertices4.add(new CyclicList<Integer>());
        System.out.println(vertices4);
        vertices4.add(new CyclicList<Integer>(0));
        System.out.println(vertices4);
        vertices4.add(new CyclicList<Integer>());
        System.out.println(vertices4);
        Triangulation t = new Triangulation(vertices4);

        System.out.println(t);

        CyclicList<CyclicList<Integer>> vertices4_2 = new CyclicList<CyclicList<Integer>>();
        vertices4_2.add(new CyclicList<Integer>());
        vertices4_2.add(new CyclicList<Integer>(3));
        vertices4_2.add(new CyclicList<Integer>());
        vertices4_2.add(new CyclicList<Integer>(1));
        Triangulation t2 = new Triangulation(vertices4_2);

        System.out.println(t2);

        Triangulation rotated_t = t.rotate((int)1);
        System.out.println(rotated_t);
        System.out.println(rotated_t.equals(t2));
        System.out.println(t.equals(t2));

        t.add_ear();
        System.out.println(t);
        t = t.rotate(1);
        System.out.println(t);
        t = t.rotate(1);
        System.out.println(t);
        t = t.rotate(1);
        System.out.println(t);
        t = t.rotate(1);
        System.out.println(t);
        t = t.rotate(1);
        System.out.println(t);

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
                tail.setNext(new CNode<E>(element));
                tail.next.setNext(head);
                head.setPrev(tail.next);
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

    private class Triangulation{

        CyclicList<CyclicList<Integer>> vertices;
        CyclicList<Integer> connection_numbers;
        int size;

        public Triangulation(CyclicList<CyclicList<Integer>> _vertices){
            vertices = _vertices;
            size = vertices.size();
        }

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
                    System.out.println(new_connections);
                    recenter_list(new_connections);
                    System.out.println(new_connections);
                    new_vertices.add(new_connections);
                } else {
                    new_vertices.add(new CyclicList<Integer>());
                }
                curr_outer_node = curr_outer_node.next();
            }

            new_vertices.rotate(n);

            return new Triangulation(new_vertices);

        }

        public void add_ear(){
            CNode<CyclicList<Integer>> head = vertices.head();
            CNode<CyclicList<Integer>> tail = vertices.head().prev();

            head.element().add(size-1);
            tail.element.addFirst(0);
            head.setPrev(new CNode<CyclicList<Integer>>(new CyclicList<Integer>()));
            tail.setNext(head.prev());

            size += 1;
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
                if (this_curr.element().size() != other_curr.element().size()){
                    return false;
                }
                if (this_curr.element().size() > 0){
                    CNode<Integer> this_inner_curr = this_curr.element().head();
                    CNode<Integer> other_inner_curr = other_curr.element().head();
                    for (int j=0; i<this_curr.element().size(); j++){
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

