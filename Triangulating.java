class Triangulating{

    public static void main(String[] args){
        Triangulating t = new Triangulating();
        t.run();
    }

    public static int int_modulo(int a, int b){
        return (int)((a % b) + b) % b;
    }

    public void run(){
        int[][] vertices4 = new int[4][4];
        vertices4[0][0] = 2;
        vertices4[2][0] = 0;
        int[] connection_numbers_4 = new int[4];
        connection_numbers_4[0] = 1;
        connection_numbers_4[1] = 0;
        connection_numbers_4[2] = 1;
        connection_numbers_4[3] = 0;
        Triangulation t = new Triangulation(vertices4, connection_numbers_4, (int)0, (int)4);

        int[][] vertices4_2 = new int[4][4];
        vertices4_2[1][0] = 3;
        vertices4_2[3][0] = 1;
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

        // change list of connections at each vertex to a linked list so we can easily insert mid-list? 
        int[][] vertices;
        int[] connection_numbers;
        int zeroindex;
        int size;

        public Triangulation(int[][] _vertices, int[] _connection_numbers, int _zeroindex, int _size){
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

            int[][] new_vertices = new int[size][size];
            for(int i=0; i<size; i++){
                int[] new_connections = new int[size];
                for(int j=0; j<connection_numbers[i]; j++){
                    new_vertices[i][j] = replacement_key[vertices[i][j]];
                }
            }

            int new_zeroindex = int_modulo(zeroindex - n, size);
            return new Triangulation(new_vertices, connection_numbers, new_zeroindex, size);
        }

        public void connect_vertices(int n1, int n2){
            n1 = int_modulo((n1 + zeroindex), size);
            n2 = int_modulo((n2 + zeroindex), size);
            int i = 0;
            while (i < vertices[n1].length && vertices[n1][i] < n2){
                i += 1; 
            }
            
        }

        public void add_ear(){

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
                for (int j = 0; j < this.connection_numbers[this_index]; j++){
                    if (this.vertices[this_index][j] != other.vertices[other_index][j]){
                        return false;
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
                for (int j=0; j<connection_numbers[i]; j++){
                    rtn = rtn + vertices[i][j] + ",";
                }
            }
            return rtn; 
        }

    }

}

