# Quickstart

from flask import Flask
# The first argument in the same of the application's module or package.
# If you are using a single module, you should use __name__
# If you are using this as an imported module, the name will be __main__
# This is needed so that Flask knows where to look for templates, static files, and so on.
app = Flask(__name__)

# Tells that the '/' url should trigger the following function
@app.route('/')
def hello_world() :
    return 'Hello, World!'

# Before running the application, you must export the FLASK_APP environment variable
'''
export FLASK_APP=Quickstart.py
'''
# In my case, I just made a bash command in the same directory to run it

# Running in development mode is very handy, as it does many things like
# Reload itself on code changes
# Provide you with a helpful debugger if things go wrong

# Simply achieve the above by setting the environment variable to 0 for False and 1 for True
# FLASK_DEBUG = 1