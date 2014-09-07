var config = function () {
    var conf = application.get("PINCH_CONFIG");
    if (!conf) {
        var pinch = require('/modules/pinch.min.js').pinch,
            address = require('/modules/address.js');
        config = require('/config/config.json'),
            pinch(config, /^/, function (path, key, value) {
                if ((typeof value === 'string') && value.indexOf('%https.ip%') > -1) {
                    return value.replace('%https.ip%', address.getAddress("https"));
                } else if ((typeof value === 'string') && value.indexOf('%http.ip%') > -1) {
                    return value.replace('%http.ip%', address.getAddress("http"));
                }
                return  value;
            });
        application.put("PINCH_CONFIG", config);
        conf = config;
    }
    return conf;
};
