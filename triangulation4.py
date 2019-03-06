import time

# Average time over 1000 runs triangulating up to 8: 1.8522831400000033

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

def triangulate(n):

    if n == 3:
        return [polygon(3)] 

    triangulations = []
    for i in range(1, n):
        n_minus_1 = triangulate(n-1)
        for triangulation in n_minus_1:
            triangulation.insert_vertex(i)
            already_found = False
            for existing_triangulation in triangulations:
                if triangulation.equals(existing_triangulation):
                    already_found = True
                    break
            if not already_found:
                triangulations.append(triangulation)

    #for triangulation in triangulations: 
    #    print(triangulation)

    print(len(triangulations))
    return triangulations

total_time = 0
for i in range(0, 1000):
    time1 = time.process_time()
    triangulate(8)
    time2 = time.process_time()
    total_time = total_time + (time2 - time1)
print(str(total_time/100))