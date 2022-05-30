import base64
import os
import sys
import random
import hashlib
import shutil

from sqlalchemy import values
import mysql.connector

from datetime import datetime
from flask import Flask, render_template, redirect, jsonify, url_for, request, session

app = Flask(__name__)

app.secret_key = "Licence-Tracker"
app.static_folder = "static"
APP_ROOT = os.path.dirname(os.path.abspath(__file__))


# Utils
def connector():
    conn = mysql.connector.connect(
        host="localhost",
        user="root",
        password="",
        database="license_tracker"
    )

    return conn


# Route for login
@app.route('/login')
def login():
    if 'user' in session:
        return redirect('index')

    return render_template('login.html')


@app.route('/')
@app.route('/index')
def index():

    if 'user' not in session:
        return redirect('login')

    conn = connector()
    query = ''' SELECT fine.id, driver.first_name, driver.last_name, location, fine.amount, fine.date_time 
                FROM fine INNER JOIN driver ON driver.id = fine.driver_id 
                WHERE status='unpaid' ORDER BY fine.id DESC LIMIT 10 '''

    cur = conn.cursor()
    cur.execute(query)
    new_unpaid_fines = cur.fetchall()

    conn = connector()
    query = ''' SELECT fine.id, driver.first_name, driver.last_name, location, fine.amount, fine.date_time 
                FROM fine INNER JOIN driver ON driver.id = fine.driver_id 
                WHERE status='unpaid' ORDER BY fine.id ASC LIMIT 10 '''

    cur = conn.cursor()
    cur.execute(query)
    old_unpaid_fines = cur.fetchall()

    return render_template('index.html', new_unpaid_fines=new_unpaid_fines,
                           old_unpaid_fines=old_unpaid_fines)


@app.route('/policemen')
def policemen():

    if 'user' not in session:
        return redirect('login')

    conn = connector()
    query = ''' SELECT * FROM policeman '''

    cur = conn.cursor()
    cur.execute(query)
    details = cur.fetchall()

    return render_template('policemen.html', details=details)


@app.route('/add_policeman')
def add_policeman():

    if 'user' not in session:
        return redirect('login')

    return render_template('add_policeman.html')


@app.route('/view_policeman_details')
def view_policeman_details():

    if 'user' not in session:
        return redirect('login')

    id = request.args.get("id", None)

    conn = connector()
    query = ''' SELECT * FROM policeman WHERE id = %s '''
    values = (int(id),)

    cur = conn.cursor()
    cur.execute(query, values)
    details = cur.fetchall()

    return render_template('view_policeman_details.html', details=details)


@app.route('/add_system_user')
def add_system_user():

    if 'user' not in session:
        return redirect('login')

    return render_template('add_system_user.html')


@app.route('/system_users')
def system_users():

    if 'user' not in session:
        return redirect('login')

    conn = connector()
    query = ''' SELECT * FROM system_users WHERE id != %s '''
    values = (int(session['user']),)

    cur = conn.cursor()
    cur.execute(query, values)
    details = cur.fetchall()

    return render_template('system_users.html', details=details)


@app.route('/view_system_user_details')
def view_system_user_details():

    if 'user' not in session:
        return redirect('login')

    id = request.args.get("id", None)

    conn = connector()
    query = ''' SELECT * FROM system_users WHERE id = %s '''
    values = (int(id),)

    cur = conn.cursor()
    cur.execute(query, values)
    details = cur.fetchall()

    return render_template('view_system_user_details.html', details=details)


@app.route('/fines')
def fines():

    if 'user' not in session:
        return redirect('login')

    conn = connector()
    query = """ SELECT fine.id, driver.first_name, driver.last_name, location, fine.amount 
                FROM fine INNER JOIN driver ON driver.id = fine.driver_id WHERE status='unpaid' ORDER BY fine.id DESC """

    cur = conn.cursor()
    cur.execute(query)
    details = cur.fetchall()

    return render_template('fines.html', details=details)


@app.route('/view_fine_details')
def view_fine_details():

    if 'user' not in session:
        return redirect('login')

    id = request.args.get("id", None)

    conn = connector()
    query = ''' SELECT * FROM fine 
                INNER JOIN driver ON driver.id = fine.driver_id
                INNER JOIN policeman ON policeman.id = fine.policeman_id WHERE fine.id = %s '''
    values = (int(id),)

    cur = conn.cursor()
    cur.execute(query, values)
    details = cur.fetchall()

    return render_template('view_fine_details.html', details=details)


@app.route('/drivers')
def drivers():

    if 'user' not in session:
        return redirect('login')

    conn = connector()
    query = ''' SELECT * FROM driver '''

    cur = conn.cursor()
    cur.execute(query)
    details = cur.fetchall()

    return render_template('drivers.html', details=details)


@app.route('/view_driver_details')
def view_driver_details():

    if 'user' not in session:
        return redirect('login')

    id = request.args.get("id", None)

    conn = connector()
    query = ''' SELECT * FROM driver WHERE id = %s '''
    values = (int(id),)

    cur = conn.cursor()
    cur.execute(query, values)
    details = cur.fetchall()

    return render_template('view_driver_details.html', details=details)


# Route for system login
@app.route('/system_login', methods=['GET', 'POST'])
def system_login():

    if request.method == "POST":

        if 'user' in session:
            return jsonify({'redirect': url_for('index')})

        else:
            email = request.form.get('email')
            psw = request.form.get('psw')

            if len(email) == 0 or len(psw) == 0:
                return jsonify({'error': "Fields are empty!"})

            else:

                psw = hashlib.md5(psw.encode()).hexdigest()

                conn = connector()
                query = ''' SELECT id, name, email FROM system_users WHERE email = %s AND psw = %s '''
                values = (str(email), str(psw))

                cur = conn.cursor()
                cur.execute(query, values)
                details = cur.fetchall()

                if len(details) < 1:
                    return jsonify({'error': "Email or password incorrect!"})

                else:
                    session['user'] = str(details[0][0])
                    return jsonify({'redirect': url_for('index')})

    return jsonify({'redirect': url_for('login')})


@app.route('/sign_out')
def sign_out():
    if 'user' in session:
        session.pop('user', None)

    return redirect("login")


# System user
@app.route('/add_new_system_user', methods=['GET', 'POST'])
def add_new_system_user():

    if request.method == "POST":

        if 'user' not in session:
            return jsonify({'redirect': url_for('login')})

        else:
            name = request.form.get('name')
            email = request.form.get('email')
            psw = request.form.get('psw')
            psw = hashlib.md5(psw.encode()).hexdigest()

            conn = connector()

            query = ''' SELECT id, name, email FROM system_users WHERE email = %s '''
            values = (str(email),)

            cur = conn.cursor()
            cur.execute(query, values)
            details = cur.fetchall()

            if len(details) > 0:
                return jsonify({'error': "System user email exist!"})

            else:
                query = ''' INSERT INTO system_users (name, email, psw) VALUES (%s, %s, %s)'''
                values = (str(name), str(email), str(psw))

                cur = conn.cursor()
                cur.execute(query, values)
                conn.commit()
                result = cur.rowcount

            if result < 1:
                return jsonify({'error': "System user not added!"})

            else:
                return jsonify({'success': "System user added!"})

    return jsonify({'redirect': url_for('index')})


@app.route('/remove_system_user', methods=['GET', 'POST'])
def remove_system_user():

    if request.method == "POST":

        if 'user' not in session:
            return jsonify({'redirect': url_for('login')})

        else:
            id = request.form.get('id')

            conn = connector()
            query = ''' DELETE FROM system_users WHERE id = %s '''
            values = (int(id), )

            cur = conn.cursor()
            cur.execute(query, values)
            conn.commit()
            result = cur.rowcount

            if result < 1:
                return jsonify({'error': "System user not removed!"})

            else:
                return jsonify({'success': "System user removed!"})

    return jsonify({'redirect': url_for('admin')})


# Policeman
@app.route('/add_new_policeman', methods=['GET', 'POST'])
def add_new_policeman():

    if request.method == "POST":

        if 'user' not in session:
            return jsonify({'redirect': url_for('login')})

        else:
            id = request.form.get('id')
            name = request.form.get('name')
            email = request.form.get('email')
            psw = request.form.get('psw')
            number = request.form.get('number')
            designation = request.form.get('designation')
            psw = hashlib.md5(psw.encode()).hexdigest()

            conn = connector()

            query = ''' SELECT id, name, email FROM policeman WHERE email = %s '''
            values = (str(email),)

            cur = conn.cursor()
            cur.execute(query, values)
            details = cur.fetchall()

            if len(details) > 0:
                return jsonify({'error': "Policeman email exist!"})

            else:
                query = ''' INSERT INTO policeman (police_id, name, email, psw, mobile, designation, status) 
                VALUES (%s, %s, %s, %s, %s, %s, %s)'''
                values = (str(id), str(name), str(email), str(psw),
                          str(number), str(designation), str("activated"))

                cur = conn.cursor()
                cur.execute(query, values)
                conn.commit()
                result = cur.rowcount

            if result < 1:
                return jsonify({'error': "Policeman not added!"})

            else:
                return jsonify({'success': "Policeman added!"})

    return jsonify({'redirect': url_for('index')})


@app.route('/update_policeman', methods=['GET', 'POST'])
def update_policeman():

    if request.method == "POST":

        if 'user' not in session:
            return jsonify({'redirect': url_for('login')})

        else:
            id = request.form.get('id')
            name = request.form.get('name')
            number = request.form.get('number')
            designation = request.form.get('designation')

            conn = connector()

            query = ''' UPDATE policeman SET name = %s, mobile = %s, designation = %s WHERE police_id = %s '''
            values = (str(name), str(number), str(designation), str(id))

            cur = conn.cursor()
            cur.execute(query, values)
            conn.commit()
            result = cur.rowcount

            if result < 1:
                return jsonify({'error': "Policeman not updated!"})

            else:
                return jsonify({'success': "Policeman updated!"})

    return jsonify({'redirect': url_for('index')})


@app.route('/deactivate_policeman', methods=['GET', 'POST'])
def deactivate_policeman():

    if request.method == "POST":

        if 'user' not in session:
            return jsonify({'redirect': url_for('login')})

        else:
            id = request.form.get('id')

            conn = connector()

            query = ''' DELETE FROM policeman WHERE police_id = %s '''
            values = (str(id), )

            cur = conn.cursor()
            cur.execute(query, values)
            conn.commit()
            result = cur.rowcount

            if result < 1:
                return jsonify({'error': "Policeman not removed!"})

            else:
                return jsonify({'success': "Policeman removed!"})

    return jsonify({'redirect': url_for('index')})


# API
# Route for driver login
@app.route('/api/driver/login', methods=['GET', 'POST'])
def driver_login():

    email = request.json['email']
    psw = request.json['psw']

    if len(email) == 0 or len(psw) == 0:
        return jsonify({'error': "Fields are empty!"})

    else:

        psw = hashlib.md5(psw.encode()).hexdigest()

        conn = connector()
        query = ''' SELECT id, first_name, email FROM driver WHERE email = %s AND psw = %s '''
        values = (str(email), str(psw))

        cur = conn.cursor()
        cur.execute(query, values)
        details = cur.fetchall()

        if len(details) < 1:
            return jsonify({"status": "error", "msg": "Email or password incorrect!"})

        else:
            return jsonify({"status": "success", "msg": "Driver login success.", "id": str(details[0][0])})


# Route for policeman login
@app.route('/api/policeman/login', methods=['GET', 'POST'])
def policeman_login():

    email = request.json['email']
    psw = request.json['psw']

    if len(email) == 0 or len(psw) == 0:
        return jsonify({'error': "Fields are empty!"})

    else:

        psw = hashlib.md5(psw.encode()).hexdigest()

        conn = connector()
        query = ''' SELECT id, name, email FROM policeman WHERE email = %s AND psw = %s '''
        values = (str(email), str(psw))

        cur = conn.cursor()
        cur.execute(query, values)
        details = cur.fetchall()

        if len(details) < 1:
            return jsonify({"status": "error", "msg": "Email or password incorrect!"})

        else:
            return jsonify({"status": "success", "msg": "Policeman login success.", "id": str(details[0][0])})


@app.route('/api/driver/register', methods=['GET', 'POST'])
def driver_register():

    if request.method == "POST":

        first_name = request.json['first_name']
        last_name = request.json['last_name']
        nic = request.json['nic']
        mobile = request.json['mobile']
        email = request.json['email']
        psw = request.json['psw']

        psw = hashlib.md5(psw.encode()).hexdigest()

        conn = connector()

        query = ''' SELECT id, first_name, email FROM driver WHERE email = %s '''
        values = (str(email),)

        cur = conn.cursor()
        cur.execute(query, values)
        details = cur.fetchall()

        if len(details) > 0:
            return jsonify({"status": "error", "msg": "Driver email exist!"})

        else:
            query = ''' INSERT INTO driver (first_name, last_name, nic, mobile, email, psw, nic_image, license_image, passport_image) 
                            VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s)'''
            values = (str(first_name), str(last_name), str(nic), str(
                mobile), str(email), str(psw), str(""), str(""), str(""))

            cur = conn.cursor()
            cur.execute(query, values)
            conn.commit()
            result = cur.rowcount

        if result < 1:
            return jsonify({"status": "error", "msg": "Driver account not created!"})

        else:
            return jsonify({"status": "success", "msg": "Driver account created!"})


@app.route('/api/driver/profile_update', methods=['GET', 'POST'])
def profile_update():

    if request.method == "POST":

        id = request.json['driver']
        first_name = request.json['first_name']
        last_name = request.json['last_name']
        nic = request.json['nic']
        mobile = request.json['mobile']

        conn = connector()

        query = ''' UPDATE driver SET first_name = %s, last_name = %s, nic = %s, mobile = %s WHERE id = %s'''
        values = (str(first_name), str(last_name),
                  str(nic), str(mobile), int(id))

        cur = conn.cursor()
        cur.execute(query, values)
        conn.commit()
        result = cur.rowcount

        if result < 1:
            return jsonify({"status": "error", "msg": "Driver account not updated!"})

        else:
            return jsonify({"status": "success", "msg": "Driver account updated!"})


@app.route('/api/driver/password_change', methods=['GET', 'POST'])
def driver_password_change():

    if request.method == "POST":

        id = request.json['id']
        psw = request.json['psw']

        psw = hashlib.md5(psw.encode()).hexdigest()

        conn = connector()

        query = ''' UPDATE driver SET psw = %s WHERE id = %s'''
        values = (str(psw), int(id))

        cur = conn.cursor()
        cur.execute(query, values)
        conn.commit()
        result = cur.rowcount

        if result < 1:
            return jsonify({"status": "error", "msg": "Driver account password not updated!"})

        else:
            return jsonify({"status": "success", "msg": "Driver account password updated!"})


@app.route('/api/driver/nic', methods=['GET', 'POST'])
def upload_nic():

    if request.method == "POST":

        id = request.json['id']
        image = request.json['image']

        if image == None:
            return jsonify({'error': "Image not uploaded"})

        else:

            uploaded_img_path = APP_ROOT + '/static/images/drivers/nic'

            if not os.path.exists(uploaded_img_path):
                os.makedirs(uploaded_img_path)

            filename = str(id) + "_driver.png"

            img_url = uploaded_img_path + "/" + filename
            with open(img_url, "wb") as fh:
                fh.write(base64.b64decode(image))

            conn = connector()

            query = ''' UPDATE driver SET nic_image = %s WHERE id = %s'''
            values = (str(filename), int(id))

            cur = conn.cursor()
            cur.execute(query, values)
            conn.commit()
            result = cur.rowcount

            if result < 1:
                return jsonify({"status": "error", "msg": "Nic image not uploaded!"})

            else:
                return jsonify({"status": "success", "msg": "Nic image uploaded!"})


@app.route('/api/driver/license', methods=['GET', 'POST'])
def upload_license():

    if request.method == "POST":

        id = request.json['id']
        image = request.json['image']

        if image == None:
            return jsonify({'error': "Image not uploaded"})

        else:

            uploaded_img_path = APP_ROOT + '/static/images/drivers/license'

            if not os.path.exists(uploaded_img_path):
                os.makedirs(uploaded_img_path)

            filename = str(id) + "_driver.png"

            img_url = uploaded_img_path + "/" + filename
            with open(img_url, "wb") as fh:
                fh.write(base64.b64decode(image))

            conn = connector()

            query = ''' UPDATE driver SET license_image = %s WHERE id = %s'''
            values = (str(filename), int(id))

            cur = conn.cursor()
            cur.execute(query, values)
            conn.commit()
            result = cur.rowcount

            if result < 1:
                return jsonify({"status": "error", "msg": "License image not uploaded!"})

            else:
                return jsonify({"status": "success", "msg": "License image uploaded!"})


@app.route('/api/driver/passport', methods=['GET', 'POST'])
def upload_passport():

    if request.method == "POST":

        id = request.json['id']
        image = request.json['image']

        if image == None:
            return jsonify({'error': "Image not uploaded"})

        else:

            uploaded_img_path = APP_ROOT + '/static/images/drivers/passport'

            if not os.path.exists(uploaded_img_path):
                os.makedirs(uploaded_img_path)

            filename = str(id) + "_driver.png"

            img_url = uploaded_img_path + "/" + filename
            with open(img_url, "wb") as fh:
                fh.write(base64.b64decode(image))

            conn = connector()

            query = ''' UPDATE driver SET passport_image = %s WHERE id = %s'''
            values = (str(filename), int(id))

            cur = conn.cursor()
            cur.execute(query, values)
            conn.commit()
            result = cur.rowcount

            if result < 1:
                return jsonify({"status": "error", "msg": "Passport image not uploaded!"})

            else:
                return jsonify({"status": "success", "msg": "Passport image uploaded!"})


@app.route('/api/driver/<id>', methods=['GET', 'POST'])
def driver_account_details(id):

    conn = connector()
    query = ''' SELECT * FROM driver WHERE id = %s '''
    values = (int(id),)

    cur = conn.cursor()
    cur.execute(query, values)
    details = cur.fetchall()

    return jsonify(details)


# Policeman
@app.route('/api/policeman/<id>', methods=['GET', 'POST'])
def policeman_account_details(id):

    conn = connector()
    query = ''' SELECT * FROM policeman WHERE id = %s '''
    values = (int(id),)

    cur = conn.cursor()
    cur.execute(query, values)
    details = cur.fetchall()

    return jsonify(details)


@app.route('/api/policeman/password_change', methods=['GET', 'POST'])
def policeman_password_change():

    if request.method == "POST":

        id = request.json['id']
        psw = request.json['psw']

        psw = hashlib.md5(psw.encode()).hexdigest()

        conn = connector()

        query = ''' UPDATE policeman SET psw = %s WHERE id = %s'''
        values = (str(psw), int(id))

        cur = conn.cursor()
        cur.execute(query, values)
        conn.commit()
        result = cur.rowcount

        if result < 1:
            return jsonify({"status": "error", "msg": "Policeman account password not updated!"})

        else:
            return jsonify({"status": "success", "msg": "Policeman account password updated!"})


# Fine
@app.route('/api/fine/driver/<id>', methods=['GET', 'POST'])
def fine_details_by_driver(id):

    conn = connector()
    query = """ SELECT * FROM fine 
                INNER JOIN driver ON driver.id = fine.driver_id
                INNER JOIN policeman ON policeman.id = fine.policeman_id WHERE fine.status = 'unpaid' AND fine.driver_id = %s """
    values = (int(id),)

    cur = conn.cursor()
    cur.execute(query, values)
    details = cur.fetchall()

    return jsonify(details)


@app.route('/api/fine/<id>', methods=['GET', 'POST'])
def fine_details_by_id(id):

    conn = connector()
    query = ''' SELECT * FROM fine 
                INNER JOIN driver ON driver.id = fine.driver_id
                INNER JOIN policeman ON policeman.id = fine.policeman_id WHERE fine.id = %s '''
    values = (int(id),)

    cur = conn.cursor()
    cur.execute(query, values)
    details = cur.fetchall()

    return jsonify(details)


@app.route('/api/fine/', methods=['GET', 'POST'])
def add_fine():

    if request.method == "POST":

        driver_id = request.json['driver_id']
        policeman_id = request.json['policeman_id']
        wrong_details = request.json['wrong_details']
        amount = request.json['amount']
        location = request.json['location']
        vehicle_no = request.json['vehicle_no']

        date_time = str(datetime.now().strftime("%Y-%m-%d"))

        conn = connector()

        query = ''' INSERT INTO fine (driver_id, policeman_id, date_time, wrong_details, amount, location, vehicle_no, status)
                        VALUES (%s, %s, %s, %s, %s, %s, %s, %s)'''
        values = (str(driver_id), str(policeman_id), str(date_time), str(
            wrong_details), str(amount), str(location), str(vehicle_no), str("unpaid"))

        cur = conn.cursor()
        cur.execute(query, values)
        conn.commit()
        result = cur.rowcount

        if result < 1:
            return jsonify({"status": "error", "msg": "Fine not added!"})

        else:
            return jsonify({"status": "success", "msg": "Fine added!"})


@app.route('/api/fine/pay', methods=['GET', 'POST'])
def pay_fine():

    if request.method == "POST":

        id = request.json['id']

        conn = connector()

        query = ''' UPDATE fine SET status = %s WHERE id = %s '''
        values = (str("paid"), int(id))

        cur = conn.cursor()
        cur.execute(query, values)
        conn.commit()
        result = cur.rowcount

        if result < 1:
            return jsonify({"status": "error", "msg": "Fine payment not success!"})

        else:
            return jsonify({"status": "success", "msg": "Fine payment success!"})


# Run
if __name__ == '__main__':

    port = "5000"
    host = "0.0.0.0"
    app.run(host=host, port=port, debug=True)
