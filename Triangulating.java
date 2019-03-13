import java.util.LinkedList;
import java.util.ListIterator;
import java.util.ArrayList;

class Triangulating{

    public static void main(String[] args){
        Triangulating t = new Triangulating();
        t.run();
    }

    public static int int_modulo(int a, int b){
        return (int)((a % b) + b) % b;
    }

    public void run(){

        LinkedList<Integer>[] vertices4 = (LinkedList<Integer>[])new Object[4];
        vertices4[0] = new LinkedList<Integer>();
        vertices4[0].add(2);
        vertices4[2] = new LinkedList<Integer>();
        vertices4[2].add(0);
        int[] connection_numbers_4 = new int[4];
        connection_numbers_4[0] = 1;
        connection_numbers_4[1] = 0;
        connection_numbers_4[2] = 1;
        connection_numbers_4[3] = 0;
        Triangulation t = new Triangulation(vertices4, connection_numbers_4, (int)0, (int)4);

        LinkedList<Integer>[] vertices4_2 = (LinkedList<Integer>[])new Object[4];
        vertices4_2[1] = new LinkedList<Integer>();
        vertices4_2[1].add(3);
        vertices4_2[3] = new LinkedList<Integer>();
        vertices4_2[3].add(1);
        int[] connection_numbers_4_2 = new int[4];
        connection_numbers_4_2[0] = 0;
        connection_numbers_4_2[1] = 1;
        connection_numbers_4_2[2] = 0;
        connection_numbers_4_2[3] = 1;
        Triangulation t2 = new Triangulation(vertices4_2, connection_numbers_4_2, (int)0, (int)4);

        System.out.println(t);
        Triangulation rotated_t = t.rotate((int)1);
        System.out.println(rotated_t);
        System.out.println(rotated_t.equals(t2));
        System.out.println(t.equals(t2));
    }

    private class CyclicList<E>{
        CNode<E> head;
        int size;

        public CyclicList(E element){
            head = new CNode<E>(E);
            head.setPrev(head);
            head.setNext(head);
            size = 1; 
        }

        // Adds a new element directly before the head node
        public void add(E element){
            CNode<E> tail = head.prev;
            tail.setNext(new CNode<E>(element));
            tail.next.setNext(head);
            size += 1;
        }

        public void rotate(int n){
            CNode<E> curr = head; 
            for (int i=0; i<n; i++){
                curr = curr.next;
            }
            head = curr;
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

        CyclicList<LinkedList<Integer>> vertices;
        int[] connection_numbers;
        int zeroindex;
        int size;

        public Triangulation(CyclicList<LinkedList<Integer>> _vertices, int[] _connection_numbers, int _size){
            vertices = _vertices;
            size = _size;
            connection_numbers = _connection_numbers;
        }

        public Triangulation rotate(int n){
        
            int[] replacement_key = new int[size];
            for(int i=0; i<size; i++){
                replacement_key[i] = (int)((i + n) % size);
            }

            CyclicList<LinkedList<Integer>> new_vertices = new CyclicList<LinkedList<Integer>>();
            CNode<LinkedList<Integer>> curr_node = vertices.head;

            for (int i=0; i<size; i++){
                if (connection_numbers[i] > 0){
                    LinkedList<Integer> new_connections = new LinkedList<Integer>();
                    LinkedList<Integer> old_connections = curr_node.element();
                    ListIterator<Integer> iter = old_connections.listIterator();
                    for (int j=0; j<connection_numbers[i]; j++){
                        new_connections.add(replacement_key[iter.next()]);
                    }
                } else {
                    new_vertices.add(new LinkedList<Integer>());
                }
                iter = iter.next();
            }

            new_vertices.rotate(n);
            return new Triangulation(new_vertices, connection_numbers, size);
        }

        public void add_ear(){
            CNode<LinkedList<Integer>> head = vertices.head();
            CNode<LinkedList<Integer>> tail = vertices.head().prev();

            head.element().add(size-1);
            tail.element().addFirst(0);
            head.setPrev(new CNode<E>(new LinkedList<Integer>()));
            tail.setNext(head.prev());

            size = size + 1;
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

            int this_index = 0;
            int other_index = 0 + (other.zeroindex - this.zeroindex);
            while (this_index < this.size){
                if (other_index < 0){
                    other_index = other_index + this.size;
                }
                if (other_index >= this.size){
                    other_index = other_index - this.size;
                }

                if (this.connection_numbers[this_index] != other.connection_numbers[other_index]){
                    return false;
                }

                if (this.connection_numbers[this_index] > 0){
                    ListIterator<Integer> iter1 = this.vertices[this_index].listIterator();
                    ListIterator<Integer> iter2 = other.vertices[other_index].listIterator();
                    for (int j=0; j<this.connection_numbers[this_index]; j++){
                        if (iter1.next() != iter2.next()){
                            return false;
                        }
                    }
                }

                this_index +=1; 
                other_index +=1;
            }
            return true;
        }

        @Override
        public String toString(){
            String rtn = "zero index: " + zeroindex;
            for (int i=0; i<size; i++){
                rtn = rtn + "\n" + i + ":";
                if (connection_numbers[i] > 0){
                    ListIterator<Integer> iter = vertices[i].listIterator();
                    while (iter.hasNext()){
                        rtn = rtn + iter.next() + ",";
                    }
                }
            }
            return rtn; 
        }

    }

}

