from flask import Flask
app = Flask(__name__)

# Simple routing
@app.route('/')
def index():
    return 'Index Page'

@app.route('/hello')
def hello():
    return 'Hello, World'

# Routing using variable rules
# The function will receive the <variable_name> as a keyword argument. Optionally, you can use a converter to specify the type of the argument like <converter:variable_name>
@app.route('/user/<username>')
def show_user_profile(username):
    return 'User %s' % username

@app.route('/post/<int:post_id>')
def show_post(post_id):
    return 'Post %d' % post_id

@app.route('/path/<path:subpath>')
def show_subpath(subpath):
    return 'Subpath %s' % subpath

# URL Building
# To build a URL to a specific function, use the url_for() function. It accepts the name of the function as its first argument and any number of keyword arguemtns, each corresponding to
# a variable part of the URL rule
# Unknown variable parts are appended to the URl as query parameters
with app.test_request_context():
    print(url_for('index')) # Prints /
    print(url_for('hello')) # Prints /hello
    print(url_for('hello', next='/')) # Prints /hello?next=/
    print(url_for('show_user_profile', username='Daniel Kim')) # Prints /user/Daniel%20Kim


# Specifying HTTP Methods and Accessing Request Data
from flask import request

@app.route('/login', methods=['GET', 'POST'])
def login():
    error = None
    if request.method == 'POST' :
        if valid_login(request.form['username'], request.form['password']):
            return log_the_user_in(request.form['username'])
        else:
            error = 'Invalid username/password'
    # The code bellow is executed if the request method was GET or the credentials were invalid
    return render_template('login.html', error=error)



# Static Files
# This is usually where the CSS and JavaScript files are coming from. Ideally, your web server is configured to serve
# them for you, but during development, Flask can do that as well. Just create a folder called static in the package or
# next to your module, and it will be available at /static on the application

# To generate URLs for static files, use the special 'static' endpoint name:
url_for('static', filename='style.css')
# The above file would have to be stored on the filesystem as static/style.css
