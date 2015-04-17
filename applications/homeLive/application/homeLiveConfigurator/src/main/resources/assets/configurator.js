/**
 * Created by ozan on 12/10/14.
 */

var apps = {};
var buttonEnableClass = 'btn-warning'; //'btn-success'
var buttonDisableClass = 'btn-default'; //'btn-danger'
var buttonVisibleClass = 'btn-success';
var buttonHiddenClass = 'btn-danger';
function registerWebSocket() {
    var ws = $.easyWebSocket("ws://" + window.location.host + "/homelive/ws");
    ws.onmessage = function (event) {
        var message = $.parseJSON(event.data);
        var appId = message.appId;
        var deviceId = message.deviceId
        var appPermission = {};
        appPermission[message.mode] = message.permission;
        if (apps[appId] === undefined) {
            apps[appId] = {};
        }
        if (apps[appId][deviceId] === undefined) {
            apps[appId][deviceId] = {};
        }
        apps[appId][deviceId][message.mode] = message.permission;
        refreshAppList(appId);
    };

}

function init() {
    registerWebSocket();
    load();
    $('.mode-selector').on('click', function (event) {
        event.preventDefault();
        var mode = $(this).attr('value');
        $.post('/homelive/mode', {"mode": mode})
            .done(function (data) {
                highlightMode(data);
            });

    });
}


function load() {
    currentMode();
    userInfo();
    alarmSettings();
    applicationSettings();
}

function highlightMode(mode) {
    $('.mode-selector').each(function (index) {
        if ($(this).attr('value') === mode) {
            $(this).addClass('active');
        } else {
            $(this).removeClass('active');
        }
    });
}

function currentMode() {
    $.get('/homelive/mode')
        .done(function (data) {
            highlightMode(data);
        });
}

function setUserEmail(data) {
    $('#userEmail').val(data);
}


function userInfo() {
    $('#userEmailForm').submit(function (event) {
        event.preventDefault();
        var form = $(this).serialize();
        $.post('/homelive/notification', form)
            .done(function (data) {
                setUserEmail(data);
            });
    });
    $.get('/homelive/notification')
        .done(function (data) {
            setUserEmail(data);
        });
}

function setButton(button, enable, disable, data) {
    if (data) {
        button.addClass('active');
        button.addClass(enable);
        button.removeClass(disable);
    } else {
        button.removeClass('active');
        button.removeClass(enable);
        button.addClass(disable);
    }

}

function toggle(button, enable, disable) {
    if (button.hasClass('active')) {
        button.removeClass(enable);
        button.addClass(disable);
    } else {
        button.addClass(enable);
        button.removeClass(disable);
    }
    return !button.hasClass('active');
}

function alarmSettings() {
    $('.alarm-button').each(function (index) {
        var button = $(this);
        var alarmType = button.attr('name');
        var url = '/homelive/alarm/' + alarmType;
        var paramName = alarmType + "Status";
        $.get(url).done(function (data) {
            setButton(button, buttonEnableClass, buttonDisableClass, data);
        });

        button.on('click', function (event) {
            //event.preventDefault();
            var param = {};
            param[paramName] = toggle(button, buttonEnableClass, buttonDisableClass, button);
            $.post(url, param)
                .done(function (data) {
                });
        })
    });
}

function applicationSettings() {
    refreshAppList();
}

function refreshAppList(appId) {
    var appList = $('#app-list');
    if (appList.attr('active') !== appId) {
        appList.empty();
        for (var key in apps) {
            var app = apps[key];
            var a = $('<a>').attr('app', key).attr('href', '#').addClass('app-selector list-group-item').text(key);
            a.on('click', function (event) {
                event.preventDefault();
                var appId = $(this).attr('app');
                highlightAppList(appId);
                refreshTable(appId);
            });
            appList.append(a);
        }
    } else {
        refreshTable(appId);
    }

}

function highlightAppList(app) {
    $('#app-list').attr('active', app);
    $('.app-selector').each(function (index) {
        if ($(this).attr('app') === app) {
            $(this).addClass('list-group-item-success');
        } else {
            $(this).removeClass('list-group-item-success');
        }
    });
}

function refreshTable(appId) {
    var app = apps[appId];
    var permissionTable = $('#permission-table-body').empty();
    permissionTable.attr('app', appId);
    console.log(app);
    for (var devId in app) {
        var dev = app[devId];
        var tr = $('<tr>');
        var device = $('<td>').text(devId);
        //var home = $('<td>').attr('mode', 'home').text(dev['home']);
        var home = $('<td>').attr('mode', 'home').append(permissionButton(appId, devId, 'home', dev['home']));
        var away = $('<td>').attr('mode', 'away').append(permissionButton(appId, devId, 'away', dev['away']));
        var night = $('<td>').attr('mode', 'night').append(permissionButton(appId, devId, 'night', dev['night']));
        var vacation = $('<td>').attr('mode', 'holidays').append(permissionButton(appId, devId, 'holidays', dev['holidays']));
        tr.append(device).append(home).append(away).append(night).append(vacation);
        permissionTable.append(tr);
    }

}

function permissionButton(appId, deviceId, mode, permission) {
    var input = $('<input>').attr('type', 'checkbox').attr('autocomplete', 'off');
    var buttonPerm = (permission === undefined || permission === "hidden") ? "hidden" : "visible";
    var permissionbutton = $('<label>').addClass('permission-button btn btn-default')
        .attr('appliId', appId)
        .attr('deviceId', deviceId)
        .attr('mode', mode)
        .attr('permission', buttonPerm)
        .append(input);
    permissionbutton.text(buttonPerm);
    setButton(permissionbutton, buttonVisibleClass, buttonHiddenClass, buttonPerm !== "hidden");
    permissionbutton.on('click', function (event) {
        var button = $(this);
        togglePermButton(button);
        var param = {
            appliId : button.attr('appliId'),
            deviceId : button.attr('deviceId'),
            mode : button.attr('mode'),
            permission : button.attr('permission') === 'visible' ? "total" : button.attr('permission')
        };
        $.post('/homelive/permission',param)
            .done(function (data) {
            });
    });
    return permissionbutton;
}

function togglePermButton(button) {
    if (button.attr('permission') === 'hidden') {
        button.attr('permission', 'visible');
        button.text('visible');
    } else {
        button.attr('permission', 'hidden');
        button.text('hidden');
    }
    toggle(button, buttonVisibleClass, buttonHiddenClass);
}