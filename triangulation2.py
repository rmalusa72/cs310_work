class triangulation: 
    def __init__(self, _vertices, _zero_index):
        self.vertices = _vertices
        self.zero_index = _zero_index
        self.size = len(self.vertices)

    def rotate(self, n):

        replacement_key = []
        for i in range(0, len(self.vertices)):
            replacement_key.append((i + n) % self.size)

        new_vertices = []
        for vertex in self.vertices:
            new_connections = []
            for connection in vertex[1]:
                new_connections.append(replacement_key[connection])
            new_vertices.append((replacement_key[vertex[0]], new_connections))
        new_zero_index = ((self.zero_index - n) % self.size)

        return triangulation(new_vertices, new_zero_index)

    def equals(self, other):
        rtn = True
        for i in range(0, len(self.vertices)):
            connections1 = self.vertices[(self.zero_index + i) % self.size][1]
            connections2 = other.vertices[(other.zero_index + i) % self.size][1]
            
            match = True
            for n in connections1:
                if n not in connections2: 
                    match = False
                    break

            if not match: 
                rtn = False
                break

        return rtn

    def add_ear(self):

        # add new diagonal from each end
        self.vertices[self.zero_index][1].append(self.size - 1)
        self.vertices[self.zero_index - 1][1].append(0)

        # insert new vertex
        if self.zero_index == 0:
            self.vertices.append((self.size, []))
        else:
            self.vertices.insert(self.zero_index, (self.size, []))
            self.zero_index = self.zero_index + 1

        self.size = self.size + 1


    def __str__(self):
        return str(self.vertices) + ", " + str(self.zero_index)

def triangulate_up_to(n):
    quad_triangulations = [triangulation([(0, [2]), (1,[]), (2,[0]), (3,[])],0), triangulation([(0, []), (1,[3]), (2,[]), (3,[1])],0)]
    last_triangulations = quad_triangulations
    for i in range(5, n+1):
        last_triangulations = triangulate(i, last_triangulations);
        print(len(last_triangulations))
        #for j in range(0, len(last_triangulations)):
        #    print(last_triangulations[j])

def triangulate(n, previous_triangulations):
    
    triangulations = []

    for triangulation in previous_triangulations: 
        triangulation.add_ear()

    i = 0
    while i < len(previous_triangulations):
        triangulation = previous_triangulations[i]
        #print(triangulation)
        triangulations.append(triangulation)
        for j in range(1, n):
            rotated_triangulation = triangulation.rotate(j)
            #print("rotated:" + str(rotated_triangulation))
            if rotated_triangulation.equals(triangulation):
                #print("self-repeat")
                break
            k = i
            while (k < len(previous_triangulations)):
                triangulation2 = previous_triangulations[k]
                if rotated_triangulation.equals(triangulation2):
                    previous_triangulations.pop(k)
                else:
                    k += 1
            triangulations.append(rotated_triangulation)
        i += 1

    return triangulations


triangulate_up_to(15)




