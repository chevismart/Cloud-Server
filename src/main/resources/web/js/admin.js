function randomString(len) {
    len = len || 32;
    var $chars = 'ABCDEFGHJKMNPQRSTWXYZabcdefhijkmnprstwxyz2345678';    /****默认去掉了容易混淆的字符oOLl,9gq,Vv,Uu,I1****/
    var maxPos = $chars.length;
    var pwd = '';
    for (i = 0; i < len; i++) {
        pwd += $chars.charAt(Math.floor(Math.random() * maxPos));
    }
    return pwd;
}

function refreshDevice() {
    $.get("http://wawaonline.net:8003/listDevice?CENTER_ID=00000000", function (data, status) {
        if (data) {
            $("#mac").html("");
            $(eval(data)).each(function () {
                $("#mac").append('<option value=' + this + '>' + this + '</option>');
            });
        }
    });
}

function checkCounter() {
    get("http://wawaonline.net:8003/counterRecord?MAC=" + $("#mac").val() + "&COIN_QTY=true&PRIZE_QTY=true");
}
function resetCounter() {
    get("http://wawaonline.net:8003/counterReset?MAC=" + $("#mac").val() + "&COIN_QTY=true&PRIZE_QTY=true");
}
function statusCounter() {
    get("http://wawaonline.net:8003/counterStatus?MAC=" + $("#mac").val());
}
function switchCounter() {
    get("http://wawaonline.net:8003/counterSwitch?MAC=" + $("#mac").val() + "&COUNTER_SWITCH=" + $("#counterStatus").val());
}
function topup() {
    //http://wawaonline.net:8003/topup?MAC=accf233b95f6&TOP_UP_REFERENCE_ID=ABCDEF1234&TOP_UP_COIN_QTY=1
    var referenceId = randomString(10);
    get("http://wawaonline.net:8003/topup?MAC=" + $("#mac").val() + "&TOP_UP_REFERENCE_ID=" + referenceId + "&TOP_UP_COIN_QTY=" + $("#coin").val());
}
function powerstatus() {
    //http://wawaonline.net:8003/powerStatus?MAC=accf233b95f6
    get("http://wawaonline.net:8003/powerStatus?MAC=" + $("#mac").val());
}
function switchPower() {
    //http://wawaonline.net:8003/powerControl?MAC=accf233b95f6&POWER_SWITCHER=true
    get("http://wawaonline.net:8003/powerControl?MAC=" + $("#mac").val() + "&POWER_SWITCHER=" + $("#powerStatus").val());
}

function get(url) {
    $.get(url, function (data, status) {
        var json = eval(data);
        $("#response").text(JSON.stringify(data))
    });
}