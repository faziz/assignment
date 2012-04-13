<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Assignment</title>        

        <script type="text/javascript" src="res/jquery-1.7.1.min.js"></script>
        <script type="text/javascript">
            $(document).ready(function(){
                $('#getuserbyname').click(function(){
                    var username = $('#username').val();
                    
                    $.ajax('${pageContext.servletContext.contextPath}/api/users/', {
                        contentType: 'application/json',
                        data: '{\"username\":\"' + username + '\"}',
                        dataType: 'json',
                        error: function(jqXHR, textStatus, errorThrown){
                            alert('Error occurred: ' + errorThrown);
                        },
                        success: function(user, textStatus, jqXHR){
                            loadUserUpdateForm(user);
                            
                            $('#deleteuser').css('visibility', 'visible');
                            $('#updateuser').css('visibility', 'visible');
                            $('#updateuserprofile').css('visibility', 'visible');
                            $('#addaddress').css('visibility', 'visible');
                            $('#getaddress').css('visibility', 'visible');
                        },
                        type: 'GET'
                    });
                });
                
                function loadUserUpdateForm(user){
                    $('#uuid').val( user.id);
                    $('#uuaid').val( user.address.id);
                    $('#uun').val( user.username);
                    $('#ufn').val( user.firstname);
                    $('#uln').val( user.lastname);
                    $('#umn').val( user.middlename);
                    $('#up').val( user.password);
                    $('#uat').val( user.apiToken);
                    loadUserAddressUpdateForm(user.address);
                }
                function loadUserAddressUpdateForm(address){
                    $('#ua1').val(address.address1);
                    $('#ua2').val(address.address2);
                    $('#us').val(address.state);
                    $('#uc').val(address.city);
                }
                
                
                $('#updateuser').click(function(){
                    var uid = $('#uuid').val();
                    var uaid = $('#uuaid').val();
                    var un = $('#uun').val();
                    var fn = $('#ufn').val();
                    var ln = $('#uln').val();
                    var mn = $('#umn').val();
                    var pwd = $('#up').val();
                    var tok = $('#uat').val();
                    var a1 = $('#ua1').val();
                    var a2 = $('#ua2').val();
                    var st = $('#us').val();
                    var ct = $('#uc').val();
                    
                    var user = getUserForUpdate(uid, uaid, un, fn, ln, mn, pwd, tok, a1, a2, ct, st);
                    
                    $.ajax('${pageContext.servletContext.contextPath}/api/users/?banckle-username-token=admin&banckle-password-token=adminpwd&banckle-api-token=token', {
                        contentType: 'application/json',
                        data: user,
                        dataType: 'json',
                        error: function(jqXHR, textStatus, errorThrown){
                            alert('Error occurred: ' + errorThrown);
                        },
                        success: function(data, textStatus, jqXHR){
                            loadUserUpdateForm(data);
                            alert('User information updated.');
                        },
                        type: 'PUT'
                    });
                });
                
                function getUserForUpdate(uid, uaid, un, fn, ln , mn, pwd, tok, add1, add2, city, stat){
                    //TODO: Use Javascript prototyping.
                    var address = getAddressForUpdate(uaid, add1, add2, city, stat);
                    return '{"address":'    + address + 
                        ',"apiToken":"'     + tok + 
                        '","password":"'    + pwd + 
                        '","username":"'    + un  + 
                        '","firstname":"'   + fn  + 
                        '","lastname":"'    + ln  + 
                        '","middlename":"'  + mn  + 
                        '","id":'           + uid + '}';
                }
                
                function getAddressForUpdate(uaid, add1, add2, city, stat){
                    return '{"id":' + uaid + ',"state":"' + stat + '","address1":"' + add1 + '","address2":"' + add2 + '","city":"' + city + '"}';
                }
                
                $('#deleteuser').click(function(){
                    var username = $('#username').val();
                    
                    $.ajax('${pageContext.servletContext.contextPath}/api/users/?banckle-username-token=admin&banckle-password-token=adminpwd&banckle-api-token=token', {
                        contentType: 'application/json',
                        data: '{\"username\":\"' + username + '\"}',
                        dataType: 'json',
                        error: function(jqXHR, textStatus, errorThrown){
                            alert('Error occurred: ' + errorThrown);
                        },
                        success: function(data, textStatus, jqXHR){                            
                            $('#deleteuser').css('visibility', 'hidden');
                            $('#updateuser').css('visibility', 'hidden');
                            $('#updateuserprofile').css('visibility', 'hidden');
                            $('#addaddress').css('visibility', 'hidden');
                            $('#getaddress').css('visibility', 'hidden');
                            
                            $('#username').val('');
                            $('#updateusertable input').val('');
                        },
                        type: 'DELETE'
                    });
                });
                
                $('#createuser').click(function(){
                    var un = $('#cun').val();
                    var fn = $('#cfn').val();
                    var ln = $('#cln').val();
                    var mn = $('#cmn').val();
                    var pwd = $('#cp').val();
                    var tok = $('#cat').val();
                    var add1 = $('#ca1').val();
                    var add2 = $('#ca2').val();
                    var city = $('#cc').val();
                    var stat = $('#cs').val();

                    var user = getNewUser(un, fn, ln, mn, pwd, tok, 
                    add1, add2, city, stat);
                    
                    $.ajax('${pageContext.servletContext.contextPath}/api/users/?banckle-username-token=admin&banckle-password-token=adminpwd&banckle-api-token=token', {
                        contentType: 'application/json',
                        data: user,
                        dataType: 'json',
                        error: function(jqXHR, textStatus, errorThrown){
                            alert('Error occurred: ' + errorThrown);
                        },
                        success: function(data, textStatus, jqXHR){
                            alert('User saved.');
                        },
                        type: 'POST'
                    });
                });

                function getNewUser(un, fn, ln , mn, pwd, tok, add1, add2, city, stat){
                    //TODO: Use Javascript prototyping.
                    var address = getNewAddress(add1, add2, city, stat);
                    return '{"address":'    + address + 
                        ',"apiToken":"'     + tok + 
                        '","password":"'    + pwd + 
                        '","username":"'    + un  + 
                        '","firstname":"'   + fn  + 
                        '","lastname":"'    + ln  + 
                        '","middlename":"'  + mn + '"}';
                }
                
                function getNewAddress(add1, add2, city, stat){
                    return '{"state":"' + stat + '","address1":"' + add1 + '","address2":"' + add2 + '","city":"' + city + '"}';
                }
                
                $('#updateuserprofile').click(function(){
                    var uid = $('#uuid').val();
                    var a1 = $('#ua1').val();
                    var a2 = $('#ua2').val();
                    var st = $('#us').val();
                    var ct = $('#uc').val();
                    
                    var address = getNewAddress(a1, a2, ct, st);
                    
                    var url = '${pageContext.servletContext.contextPath}/api/users/' + 
                        uid + 
                        '/address/?banckle-username-token=admin&banckle-password-token=adminpwd&banckle-api-token=token';
                    
                    $.ajax(url, {
                        contentType: 'application/json',
                        data: address,
                        dataType: 'json',
                        error: function(jqXHR, textStatus, errorThrown){
                            alert('Error occurred: ' + errorThrown);
                        },
                        success: function(address, textStatus, jqXHR){
                            alert('Address updated.');
                            loadUserAddressUpdateForm(address);
                        },
                        type: 'PUT'
                    });
                });
                $('#addaddress').click(function(){
                    var uid = $('#uuid').val();
                    var a1 = $('#ua1').val();
                    var a2 = $('#ua2').val();
                    var st = $('#us').val();
                    var ct = $('#uc').val();
                    
                    var address = getNewAddress(a1, a2, ct, st);
                    
                    var url = '${pageContext.servletContext.contextPath}/api/users/' + 
                        uid + 
                        '/address/?banckle-username-token=admin&banckle-password-token=adminpwd&banckle-api-token=token';
                    
                    $.ajax(url, {
                        contentType: 'application/json',
                        data: address,
                        dataType: 'json',
                        error: function(jqXHR, textStatus, errorThrown){
                            alert('Error occurred: ' + errorThrown);
                        },
                        success: function(address, textStatus, jqXHR){
                            alert('Address added.');
                            loadUserAddressUpdateForm(address);
                        },
                        type: 'POST'
                    });
                });
                $('#getaddress').click(function(){
                    var uid = $('#uuid').val();
//                    var a1 = $('#ua1').val();
//                    var a2 = $('#ua2').val();
//                    var st = $('#us').val();
//                    var ct = $('#uc').val();
//                    
//                    var address = getNewAddress(a1, a2, ct, st);
                    
                    var url = '${pageContext.servletContext.contextPath}/api/users/' + 
                        uid + 
                        '/address';
                    
                    $.ajax(url, {
                        contentType: 'application/json',
                        dataType: 'json',
                        error: function(jqXHR, textStatus, errorThrown){
                            alert('Error occurred: ' + errorThrown);
                        },
                        success: function(address, textStatus, jqXHR){
                            loadUserAddressUpdateForm(address);
                        },
                        type: 'GET'
                    });
                });
            });            
        </script>
    </head>
    <body>
        <h1>Assignment!</h1>

        <div>
            <div style="float: left; border-top: 1px solid black; border-right: 1px solid black; border-bottom: 1px solid black; border-left: 1px solid black; width: 49%">
                <div style="font-weight: bold;">&nbsp;Get\Update\Delete User Test</div>
                <table border="0">
                    <tr>
                        <td>Username</td>
                        <td><input type="text" id="username" name="username" value="" /></td>
                    </tr>
                    <tr>
                        <td colspan="2" >
                            <input type="button" id="getuserbyname" value="Get user" />
                            <input type="button" id="updateuser" value="Update user" style="visibility: hidden" />
                            <input type="button" id="deleteuser" value="Delete user" style="visibility: hidden" />
                            <input type="button" id="updateuserprofile" value="Update user profile" style="visibility: hidden" />
                            <input type="button" id="addaddress" value="Add address" style="visibility: hidden" />
                            <input type="button" id="getaddress" value="Get address" style="visibility: hidden" />
                        </td>
                    </tr>
                </table>
                <div id="userdetail">
                    <table id="updateusertable">
                        <input type="hidden" id="uuid"  value="" />
                        <input type="hidden" id="uuaid" value="" />
                        <tr>
                            <td>Username</td>
                            <td><input type="text" id="uun" value="" disabled/></td>
                        </tr>
                        <tr>
                            <td>First Name</td>
                            <td><input type="text" id="ufn" value="" /></td>
                        </tr>
                        <tr>
                            <td>Last Name</td>
                            <td><input type="text" id="uln" value="" /></td>
                        </tr>
                        <tr>
                            <td>Middle Name</td>
                            <td><input type="text" id="umn" value="" /></td>
                        </tr>
                        <tr>
                            <td>Password</td>
                            <td><input type="password" id="up" value="" /></td>
                        </tr>
                        <tr>
                            <td>API Token</td>
                            <td><input type="text" id="uat" value="" /></td>
                        </tr>
                        <tr><td colspan="2">Address</td></tr>
                        <tr>
                            <td>Address 1</td>
                            <td><input type="text" id="ua1" value="" /></td>
                        </tr>
                        <tr>
                            <td>Address 2</td>
                            <td><input type="text" id="ua2" value="" /></td>
                        </tr>
                        <tr>
                            <td>City</td>
                            <td><input type="text" id="uc" value="" /></td>
                        </tr>
                        <tr>
                            <td>State</td>
                            <td><input type="text" id="us" value="" /></td>
                        </tr>
                    </table>
                </div>
            </div>
            <div style="float: right; border-top: 1px solid black; border-right: 1px solid black; border-bottom: 1px solid black; border-left: 1px solid black; width: 49%">
                <div style="font-weight: bold;">&nbsp;Create User Test</div>                
                <table border="0">
                    <tr>
                        <td>Username</td>
                        <td><input type="text" id="cun" name="cun" value="" /></td>
                    </tr>
                    <tr>
                        <td>First Name</td>
                        <td><input type="text" id="cfn" name="cfn" value="" /></td>
                    </tr>
                    <tr>
                        <td>Last Name</td>
                        <td><input type="text" id="cln" name="cln" value="" /></td>
                    </tr>
                    <tr>
                        <td>Middle Name</td>
                        <td><input type="text" id="cmn" name="cmn" value="" /></td>
                    </tr>
                    <tr>
                        <td>Password</td>
                        <td><input type="password" id="cp" name="cp" value="" /></td>
                    </tr>
                    <tr>
                        <td>API Token</td>
                        <td><input type="text" id="cat" name="cat" value="" /></td>
                    </tr>
                    <tr><td colspan="2">Address</td></tr>
                    <tr>
                        <td>Address 1</td>
                        <td><input type="text" id="ca1" name="ca1" value="" /></td>
                    </tr>
                    <tr>
                        <td>Address 2</td>
                        <td><input type="text" id="ca2" name="ca2" value="" /></td>
                    </tr>
                    <tr>
                        <td>City</td>
                        <td><input type="text" id="cc" name="cc" value="" /></td>
                    </tr>
                    <tr>
                        <td>State</td>
                        <td><input type="text" id="cs" name="cs" value="" /></td>
                    </tr>

                    <tr>
                        <td colspan="2" >
                            <input type="button" id="createuser" value="Create" />
                        </td>
                    </tr>
                </table>
            </div>
        </div>
    </body>
</html>
