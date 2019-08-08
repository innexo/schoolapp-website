from flask import Flask
import mysql.connector


mydb = mysql.connector.connect(
    user="root",
    passwd="root",
    database="schoolapp"
)

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

@app.route('/api/user/new/')
def newUser(
