from flask import render_template

@app.route('/hello/')
@app.route('/hello/<name>')
def hello(name=None):
    return render_template('hello.html', name=name)

# Case 1, if your application is a module, the following folder structure is expected
'''
/application.py
/templates
    /hello.html
'''

# Case 2, If your application is a package, it should be inside your package
'''
/application
    /__init__.py
    /templates
        /hello.html
'''
