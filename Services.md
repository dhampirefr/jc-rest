# Introduction #

Here is a list of currently available services.


# Details #

## Nodes ##

### XML/JSON ###

| **Property** | **Type** | **Description** |
|:-------------|:---------|:----------------|
| nodePath | path | the path to the root node |
| nodeDepth | query | the depth of node(s) to retrieve beginning with the root node specified by the node path (1 - root node, 2 - children, 3 grandchildren, etc) |
| nodeType | query | only return nodes of a particular node type |
| propertiesToShow | query | only return specific properties of the return node(s) |
| includeAllProperties | query | return all properties of the return node(s) |
| includePrimaryItem | query | return the primary item (node) of the return node(s) |
| includePrimaryItemDescendants | query | if includePrimaryItem is true, return its descendants as well |
| includeAllChildren | query | return all children (nodes) of the return node(s) |


/jc-rest/services/path/nodes/{nodePath}

/jc-rest/services/search/nodes/{nodePath}

### HTML/XML/JSON via FTL ###

/jc-rest/services/template/ftl/{template}/nodes/{nodePath}

### ATOM ###

/jc-rest/services/feed/{nodePath}

## Events ##

### XML/JSON ###

/jc-rest/services/events{nodePath}