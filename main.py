from flask import Flask, request, abort
import mysql.connector
import hashlib
import base64
import os
from threading import Lock


cnx = mysql.connector.connect(
    user="root",
    passwd="root",
    database="schoolapp",
    autocommit=True
)

cursor_lock = Lock()
cursor = cnx.cursor(prepared=True)


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

def userAdd(email, name, password_hash, ring):
    with cursor_lock:
        try:
            cursor.execute('INSERT INTO user(email, name, password_hash, ring) values(%s, %s, %s, %s)', (email, name, password_hash, ring,))
            return userGetByEmail(email)
        except:
            return None

def userDel(email):
    with cursor_lock:
        try:
            cursor.execute('DELETE FROM user WHERE email=%s', (email,))
        except:
            return None
    rows = userGetByEmail(email)
    finally:
        cursor_lock.release()
    return rows

def userExistsByEmail(email):
    cursor_lock.acquire()
    try:
        cursor.execute('SELECT * FROM user WHERE email=%s', (email,))
    except:
        return None
    finally:
        cursor_lock.release()
    rowcount = cursor.fetchall().rowcount > 0
    return rowcount

def userGetByEmail(email):
    cursor_lock.acquire()
    try:
        cursor.execute('SELECT * FROM user WHERE email=%s', (email,))

    rows = cursor.fetchall()
    cursor_lock.release()
    return rows

# Post
#    id
#    requester_id
#    approver_id
#    approved
#    title
#    text

def postAdd(requester_id, approver_id, title, text):
    cursor_lock.acquire()
    cursor.execute('INSERT INTO post(requester_id, approver_id, title, text) values(%s, %s, %s, %s, %s)', (requester_id, approver_id, title, text,))
    cnx.commit()
    id = cursor.lastrowid
    cursor_lock.release()
    return postGetById(id)

def postDel(id):
    cursor_lock.acquire()
    cursor.execute('DELETE FROM post WHERE id=%s', (id,))
    id = cursor.lastrowid
    cnx.commit()
    cursor_lock.release()
    return postGetById(id)

def postExistsById(id):
    cursor_lock.acquire()
    cursor.execute('SELECT * FROM post WHERE id=%s', (id,))
    exists = cursor.fetchall().rowcount > 0
    cursor_lock.release()
    return exists

def postGetById(id):
    cursor_lock.acquire()
    cursor.execute('SELECT * FROM post WHERE id=%s', (id,))
    post = cursor.fetchall()
    cursor_lock.release()
    return post

# Club
#    id
#    creator_id
#    title
#    image_blob
#    text

def clubAdd(creator_id, title, image_blob, text):
    cursor_lock.acquire()
    cursor.execute('INSERT INTO club(creator_id, title, image_blob, text) values(%s, %s, %s, %s)', (creator_id, title, image_blob, text,))
    id = cursor.lastrowid
    cursor_lock.release()
    return clubGetById(id)

def clubDel(id):
    cursor_lock.acquire()
    cursor.execute('DELETE FROM club WHERE id=%s', (id,))
    id = cursor.lastrowid
    cnx.commit()
    cursor_lock.release()
    return clubGetById(id)

def clubExistsById(id):
    cursor_lock.acquire()
    cursor.execute('SELECT * FROM club WHERE id=%s', (id,))
    exists = cursor.fetchall().rowcount > 0
    cursor_lock.release()
    return exists

def clubGetById(id):
    cursor_lock.acquire()
    cursor.execute('SELECT * FROM club WHERE id=%s', (id,))
    club = cursor.fetchall()
    cursor_lock.release()
    return club


# please provide email, name, password
@app.route('/api/user/new/')
def newUser():
    email = normalize_str(request.args.get('email'))
    name = normalize_str(request.args.get('name'))
    password = normalize_str(request.args.get('password'))
    if not is_empty(email) and not is_empty(name) and not is_empty(password):
        userAdd(email, name, hash_password(password), 2)
        return "nice"
    else:
        abort(400)
    return 0



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
    salt = hashlib.sha256(os.urandom(32)).hexdigest().encode('ascii')
    pwdhash = hashlib.pbkdf2_hmac('sha512', password.encode('utf-8'),
                                salt, 100000)
    pwdhash = base64.b64encode(pwdhash)
    return (salt + pwdhash).decode('ascii')

# Verify a stored password against one provided by user
def verify_password(password_hash, password):
    salt = password_hash[:32]
    password_hash = password_hash[32:]
    pwdhash = hashlib.pbkdf2_hmac('sha512',
                                  password.encode('utf-8'),
                                  salt.encode('ascii'),
                                  100000)
    pwdhash = base64.b64encode(pwdhash).decode('ascii')
    return pwdhash == password_hash


if __name__ == '__main__':
    app.run(debug=True, port=5000) #run app in debug mode on port 5000

