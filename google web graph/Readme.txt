Save (and unzip) the web-Google.zip file and run the jar on it.

In eclipse do this: under run -> run configuration in the arguments tab. There's a program arguments section where you can enter them.
You need to first enter the filesystem path to the source file (the web-Google.txt file) and second the path to a (non existent) file into which you want the output to be written.
The two arguments are separated by a space. If any of the two paths contains a space, then surround that one argument with quotes (" ").
Assuming you saved web-Google.txt in the src directory of the project, then entering src/web-Google.txt as first argument
and src/output.txt as second argument should leave you with a file output.txt in the src directory after running the tool.
-----------------------------------------------------------------------------------------------------------------------------------

About the dataset:

In the web-Google.txt file there are always two numbers per line. 
Each is the ID of a node. A line with two numbers represents a link from the node with the ID <first number> to the node with the ID <second number>.
Nodes do not contain any data (apart from their ID).
The create the nodes I use the Neo4J MERGE statement. It first tries to find a node with the exact parameters as specified, and returns it if found. 
Otherwise it creates a new node with the specified parameters and returns it.
With the two nodes found or created, I use the Neo4J CREATE statement to create the link / relation between them.

So the line 1 2 turns into:

MERGE (a:Node {id: 1})
MERGE (b:Node {id: 2})
CREATE (a)-[:LINK]->(b)


I cannot just unconditionally create the nodes with CREATE because then I'd be having duplicated nodes (multiple nodes with the same id).
The above is for a single link. To create more than a single link in one go, I separate the statements of each link with WITH count(*) as dummy. 
This is a common trick to do that (found through some references online), it basically creates a variable named dummy which holds the count of nodes affected of the previous statement. 
It's important to use count(*) because this reduces to a single value (the count) and not to a set of nodes. In the later case, this would lead to repeated execution of the following Neo4J statements, and thus duplicated nodes and links.

-----------------------------------------------------------------------------------------------------------------------------
ER:

It's also not really spectacular. There are just nodes after all, and the only relation is a possible link to another node. Or rather nodes.
Thus the cardinality of that link is 0 to infinity. Basically this:

_______    0...
|      |   <-----
| NODE |   0... |
_______    --------


