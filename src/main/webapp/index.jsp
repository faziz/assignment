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
                    
                    $.ajax('${pageContext.servletContext.contextPath}/api/users/?banckle-username-token=123', {
                        contentType: 'application/json',
                        data: '{\"username\":\"' + username + '\"}',
                        dataType: 'json',
                        error: function(jqXHR, textStatus, errorThrown){
                            alert('Error occurred: ' + errorThrown);
                        },
                        success: function(data, textStatus, jqXHR){
                            $('#un').append(data.firstname);
                            $('#ln').append(data.lastname);
                        },
                        method: 'GET'
                    });
                });
            });            
        </script>
    </head>
    <body>
        <h1>Assignment!</h1>

        <div style="border-top: 1px solid black; border-right: 1px solid black; border-bottom: 1px solid black; border-left: 1px solid black; width: 50%">
            <div style="font-weight: bold;">Get User Test</div>
            <form id="form1">
                <table border="0">
                    <tr>
                        <td>Username</td>
                        <td><input type="text" id="username" name="username" value="" /></td>
                    </tr>
                    <tr>
                        <td colspan="2" >
                            <input type="button" id="getuserbyname" value="Get user" />
                        </td>
                    </tr>
                </table>
            </form>
            <div id="userdetail">
                <table>
                    <tr>
                        <td>First Name: </td><td id="un"></td>
                    </tr>
                    <tr>
                        <td>Last Name: </td><td id="ln"></td>
                    </tr>
                </table>
            </div>
        </div>

    </body>
</html>
