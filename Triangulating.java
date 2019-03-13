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

    private class Triangulation{

        LinkedList<Integer>[] vertices;
        int[] connection_numbers;
        int zeroindex;
        int size;

        public Triangulation(LinkedList<Integer>[] _vertices, int[] _connection_numbers, int _zeroindex, int _size){
            vertices = _vertices;
            zeroindex = _zeroindex;
            size = _size;
            connection_numbers = _connection_numbers;
        }

        public Triangulation rotate(int n){
        
            int[] replacement_key = new int[size];
            for(int i=0; i<size; i++){
                replacement_key[i] = (int)((i + n) % size);
            }

            LinkedList<Integer>[] new_vertices = (LinkedList<Integer>[])new Object[size];
            for(int i=0; i<size; i++){
                if (connection_numbers[i] > 0){
                    new_vertices[i] = new LinkedList<Integer>();
                    ListIterator<Integer> iter = vertices[i].listIterator(0);
                    for (int j=0; j<connection_numbers[i]; j++){
                        new_vertices[i].add(replacement_key[iter.next()]);
                    }
                }

            }

            int new_zeroindex = int_modulo(zeroindex - n, size);
            return new Triangulation(new_vertices, connection_numbers, new_zeroindex, size);
        }

        public void connect_vertices(int n1, int n2){
            int n1_index = int_modulo((n1 + zeroindex), size);
            int n2_index = int_modulo((n2 + zeroindex), size);
            if (vertices[n1_index] == null){
                vertices[n1_index] = new LinkedList<Integer>();
            }
            if (vertices[n2_index] == null){
                vertices[n2_index] = new LinkedList<Integer>();
            }
            ListIterator<Integer> iter1 = vertices[n1_index].listIterator();
            while (iter1.hasNext()){
                int n = iter1.next();
                if (n <= n2){
                    iter1.previous();
                    break;
                } 
            }
            iter1.add(n2);

            ListIterator<Integer> iter2 = vertices[n2_index].listIterator();
            while (iter2.hasNext()){
                int n = iter2.next();
                if (n <= n1){
                    iter2.previous();
                    break;
                } 
            }
            iter2.add(n1);
        }

        public void add_ear(){
            connect_vertices(zeroindex, int_modulo(zeroindex - 1, size));
            // Make a copy starting from zero_index (so new zero_index is 0, and one bigger) and return?
            // Or like ... make it all linked lists so insertion is easier .. what am I doing that's array specific? 
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

