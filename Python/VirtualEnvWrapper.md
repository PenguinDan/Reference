# VirtualEnvWrapper Setup

1. Install python 3 
   - `sudo apt install python3.7`
2. Install pip 
   - `sudo apt install python3-pip`
3. Install virtual
   - `sudo pip3 install virtualenv`
   - `sudo pip install virtualenvwrapper`
4. Set where all your environments are going to be stored
   - `export WORKON_HOME=~/Environment_Storage_Dir`
5. Set further environment variables for server
   - For Google cloud, do the folowing:
     - `export VIRTUALENVWRAPPER_PYTHON=/usr/bin/python3.x`
     - `export VIRTUALENVWRAPPER_VIRTUALENV=~/.local/bin/virtualenv`
     - `export VIRTUALENVWRAPPER_VIRTUALENV_ARGS='--no-site-packages'`
   - For AWS, do the following:
     - `export VIRTUALENVWRAPPER_PYTHON=/usr/bin/python3.x`
     - `export VIRTUALENVWRAPPER_VIRTUALENV=/usr/local/bin/virtualenv`
     - `export VIRTUALENVWRAPPER_VIRTUALENV_ARGS='--no-site-packages'`
6. Allow virtualenvwrapper commands to work
   - `source /usr/local/bin/virtualenvwrapper.sh`

