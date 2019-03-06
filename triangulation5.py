import time

# average time over 1000 runs triangulating up to 8: 0.0005511499999999994

class polygon:
    def __init__(self, size):
        self.head = vertex(None, 0)
        self.size = size
        cur = self.head
        for i in range(1, size):
            cur.next = vertex(None, i)
            cur = cur.next
        cur.next = self.head

    def insert_vertex(self, index):
        cur = self.head
        for i in range(1, index):
            cur = cur.next
        cur.next = vertex(cur.next, index)
        endpt1 = cur
        endpt2 = cur.next.next
        cur = cur.next
        for i in range(index, self.size):
            cur = cur.next
            cur.n = cur.n + 1
        endpt1.add_connection(endpt2)
        endpt2.add_connection(endpt1)
        self.size = self.size + 1

    # index is the n of the vertex we insert before
    # TODO: SOMETHING IS BROKEN HERE
    def insert_vertex_into_copy(self, index):

        #print(str(self))
        #print(str(index))

        copy = polygon(self.size)
        copy_vertices = copy.unroll()
        cur = self.head

        # copy connections over to copy
        # renumber vertices after index
        # insert vertex at index
        # add diagonal connecting pre-index and post-index

        for i in range(0, self.size):
            for connection in cur.connections:
                copy_vertices[i].connections.append(copy_vertices[connection.n])
            if i >=index:
                copy_vertices[i].n = copy_vertices[i].n + 1
            cur = cur.next

        endpt1 = copy_vertices[index-1]
        endpt2 = endpt1.next
        endpt1.next = vertex(endpt2, index)
        endpt1.add_connection(endpt2)
        endpt2.add_connection(endpt1)
        copy.size = copy.size + 1
        return copy

    # try just having them in an array the whole time? since we have to move stuff when we insert a vertex anyway
    def unroll(self):
        vertices = []
        cur = self.head
        for i in range(0, self.size):
            vertices.append(cur)
            cur = cur.next
        return vertices

    def __str__(self):
        rtn = ""
        cur = self.head
        for i in range(0, self.size):
            rtn = rtn + "vertex: " + str(cur.n) + "\n"
            rtn = rtn + "connections: \n"
            for connection in cur.connections:
                rtn = rtn + str(connection.n) + "\n"
            cur = cur.next
        return rtn

    def equals(self, other):
        cur1 = self.head
        cur2 = other.head
        for i in range(0, self.size):
            if len(cur1.connections) != len(cur2.connections):
                return False
            for j in range(0, len(cur1.connections)):
                if (cur1.connections[j].n != cur2.connections[j].n):
                    return False
            cur1 = cur1.next
            cur2 = cur2.next
        return True


class vertex:
    def __init__(self, next, n):
        self.next = next
        self.n = n
        self.connections = []

    def add_connection(self, connection):
        i = 0
        while (i < len(self.connections) and self.connections[i].n < connection.n):
            i = i + 1
        self.connections.insert(i, connection)

triangulation_sets = {}

def triangulate(n):

    #print(triangulation_sets)

    if n == 3:
        return [polygon(3)] 

    if n in triangulation_sets.keys():
        return triangulation_sets[n]

    triangulations = []
    for i in range(1, n):
        #print("n = " + str(n))
        n_minus_1 = triangulate(n-1)
        for triangulation in n_minus_1:
            #print("inserting " + str(i) + "th vertex")
            new_triangulation = triangulation.insert_vertex_into_copy(i)
            already_found = False
            for existing_triangulation in triangulations:
                if new_triangulation.equals(existing_triangulation):
                    already_found = True
                    break
            if not already_found:
                triangulations.append(new_triangulation)

    #for triangulation in triangulations: 
    #    print(triangulation)

    if not n in triangulation_sets.keys():
        triangulation_sets[n] = triangulations

    #print(len(triangulations))
    return triangulations

#t = triangulate(5)
#for triangulation in t:
#    print(triangulation)

i=3
while(true):
    time1 = time.process_time()
    t = triangulate(i)
    time2 = time.process_time()
    print("time for " + str(i) + ":" + str(time2 - time1))
    print(len(t))

# total_time = 0
# for i in range(0, 1000):
#     time1 = time.process_time()
#     print(len(triangulate(8)))
#     time2 = time.process_time()
#     total_time = total_time + (time2 - time1)
# print(str(total_time/100))