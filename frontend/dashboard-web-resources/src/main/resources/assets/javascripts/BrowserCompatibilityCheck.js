//TODO refactor as a module

var degradedModeCookieName = "icasa.degradedModeAccepted";
var degradedModeAccepted = getCookie(degradedModeCookieName);

if ((degradedModeAccepted != "true") && !(BrowserDetect.browser == "Firefox")) {
    var modal = $('<div></div>').addClass("modal hide fade");
    var header = $('<div></div>').addClass("modal-header");
    var body = $('<div></div>').addClass("modal-body");
    var warning = $('<h4>Warning</h4>');
    var text = $('<div></div>').addClass("alert alert-error").append(warning, "Your browser is not officially supported, It may works but there is no guarantee");
    var footer = $('<div></div>').addClass("modal-footer");
    var doc = $('<a href="http://adeleresearchgroup.github.com/iCasa-Simulator/" target="_blank">See documentation</a>').addClass("btn btn-primary");
    var con = $('<button data-dismiss="modal" aria-hidden="true">Close</button>').addClass("btn");

    con.click(function() {
        createCookie(degradedModeCookieName, "true");
    });

    body.append(text);
    footer.append(doc, con);
    modal.append(body, footer);
    $('body').append(modal);
    modal.modal('toggle');
};
