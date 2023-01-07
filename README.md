# Hyperdrama generator
Used to generate hyperdrama HTML structures.  
Given one or more input files in a folder, produces a network of interconnected HTML-pages, 
one for each declared chapter.  
Each chapter has a key and a value for its chronological order in the story, and will by default link to the nearest previous and following chapter in the same story line, if they exist.  
E.g. story line BLUE can have chapters with order values 1, 5, 10, 50, and 100. Chapter BLUE:10 will link back to BLUE:5 and forwards to BLUE:50.
Additionally, each chapter may also link to however many other chapters across all declared story lines.  

Chapter texts can implement two types of placeholders:
- Text injection: formatted as ``$<KEY>``, e.g. ``$RED_INTRO``, inserts snippets of text that have been
declared and stored for that key. 
  - Text injection can be useful to maintain consistency for texts that are supposed to be identical
   across multiple story lines, like dialogue, broadcasts etc. 
 Declaring such texts once means you only need to update it in one place.
  - declaring ``RED_INTRO: "It was a dark and stormy night"`` and then declaring a chapter text to say   
    ```
    He sat down by his typewriter to write on his novel: "$RED_INTRO"...
    ```  
    will parse the chapter's text into  
    ```
    He sat down by his typewriter to write on his novel: "It was a dark and stormy night"...
    ```
    The same text injection can then be re-used across multiple chapters and separate story lines:  
    ```
    She picked up the manuscript from his desk and grimaced at the words as she started reading.
    '"$RED_INTRO"... what a cliché.' she sighed and put the manuscript back on top the desk. 
    ```
    Naturally becomes
    ```
    She picked up the manuscript from his desk and grimaced at the words as she started reading.
    '"It was a dark and stormy night"... what a cliché.' she sighed and put the manuscript back on top the desk. 
    ``` 
- In-text chapter links: formatted as ``@<KEY>(<TEXT>)``, inserts a hyperlink with the declared
text to another story line's chapter whose order value is the highest value 
above or equal to the chapter which this link is inserted into
  - adding ``:<ORDER>`` at the end will override the current chapter's order value,
 and will instead try and find the chapter 
 with the order value equal to or closest value below the declared value.  
   Example: 
    ```
    She looked up from her seat by the window of the café, a flash of red in her periphery had caught her eye.
    "@RED(The lady in red):100"... she mumbled to herself, eyeing a middle-aged woman in a scarlet red dress strutting
    down the sidewalk on the other side of the street.
    ```
    Would insert a hyperlink with the text `The lady in red` to the chapter in the RED story line
 whose order value is at or closest below the value `100`. 

All input files for a hyperdrama project contains one instance of the `Story` type declared below.  
It should be noted that all chapters across all story lines can be declared in a single file.  
Inversely, a story can also be declared across multiple files. 
```
type ChapterId = {
    title: string;
    key: string; // story-line key
    order: number; // chronological order of chapter
}

type Story = {
    fileName: string;
    meta: {
        title: string;
        version: string;
        description: string;
        author: string[];
    },
    chapters: ChapterId & { 
        text: string;
        back?: ChapterId[],
        forward?: ChapterId[]
    }[],
    texts: { // key - value map to store values for text injection
        [key: string]: string;
    }
}
```
All files in a project folder will be read and merged (with potential overwrites) into a single story object
that is then used to build the structure of the story.  
A log file is also produced that outlines the building process and eventual overwrites and potential issues.  


