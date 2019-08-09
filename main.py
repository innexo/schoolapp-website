from flask import Flask, request, abort
import mysql.connector
import hashlib, binascii, os


cnx = mysql.connector.connect(
    user="root",
    passwd="root",
    database="schoolapp"
)
cursor = cnx.cursor()

app = Flask(__name__)


# Tasks
#    Manage 3 layers of user
#       Sysadmin
#       Teacher
#       Student
#    Manage Posts
#       PostRequest Object (Made by New Post page)
#           Automatically approved for principal and teacher
#           Must be manually approved for student
#       Approval process
#           Once the postRequest is approved
#               New post is inserted, old postrequest is deleted
#       At time of post approval, it is sent to client as an Firebase Cloud Message
#    Manage Clubs
#       Only teacher or sysadmin may add more clubs / modify them
#       Clubs have some text and a picture (stored as blob)

# User
#    email
#    name
#    password_hash
#    ring

def addUser(email, name, password_hash, ring):
    cursor.execute('INSERT INTO user(email, name, password_hash, ring) values(?, ?, ?, ?)', (email, name, password_hash, ring))
    cursor.commit()
    return

def delUser(email):
    cursor.execute('DELETE FROM user WHERE email=?', (email))
    cursor.commit()
    return

def userExistsByEmail(email):


# Post
#    id
#    requester_id
#    approver_id
#    approved
#    title
#    text

# Club
#    id
#    creator_id
#    title
#    image_blob
#    text


# please provide email, name, password
@app.route('/api/user/new/')
def newUser():
    email = normalize_str(request.args.get('email'))
    name = normalize_str(request.args.get('name'))
    password = normalize_str(request.args.get('password'))
    if not is_empty(email) and not is_empty(name) and not is_empty(password):
        # now we check if there's not already a user by that name and email

    else:
        abort(400)





# Utils

# checks if string exists or is whitespace
def is_empty(str):
    return str is None or str == ''

# normalize string
def normalize_str(str):
    return None if str is None else str.strip().lower()

# SQL escape string
def sql_esc_str(str):
    return '\'' + str.replace('\'', '\\\'') + '\''

# Password utilities

# Hash a password for storing.
def hash_password(password):
    salt = hashlib.sha256(os.urandom(60)).hexdigest().encode('ascii')
    pwdhash = hashlib.pbkdf2_hmac('sha512', password.encode('utf-8'),
                                salt, 100000)
    pwdhash = binascii.hexlify(pwdhash)
    return (salt + pwdhash).decode('ascii')

# Verify a stored password against one provided by user
def verify_password(password_hash, password):
    salt = password_hash[:64]
    password_hash = password_hash[64:]
    pwdhash = hashlib.pbkdf2_hmac('sha512',
                                  password.encode('utf-8'),
                                  salt.encode('ascii'),
                                  100000)
    pwdhash = binascii.hexlify(pwdhash).decode('ascii')
    return pwdhash == password_hash


if _name == '__main_':
    app.run(debug=True, port=5000) #run app in debug mode on port 5000

