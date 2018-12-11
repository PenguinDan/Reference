# Some Methods of Handling JSON

## Simple Open File and Create JSON Structure
```
import json

file = open('file_name.json', 'w')
dict = {'key' : value}
# Create the Stringified Json Format
json_dump = json.dumps(dict)
# Write it in to a file
file.write(json_dump)
# Close the file
file.close()
```