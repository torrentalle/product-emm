var startup = (function () {

    var log = new Log();
    var db;
    var driver, user, apimgr;
    var sqlscripts = require('/sqlscripts/db.js');
    var userModule = require('/modules/user.js').user;
    var apimgrModule = require("/modules/apimgr.js").apimgr;
    var carbon = require('carbon');
    var common = require("/modules/common.js");

    var module = function (dbs) {
        db = dbs;
        driver = require('driver').driver(db);
        user = new userModule(db);
        apimgr = new apimgrModule(db);
    };


    // prototype
    module.prototype = {
        constructor: module,

        //this executes after user loggedin
        onUserLogin: function (ctx) {

            log.debug("USER LOGGED " + stringify(ctx));

            if (ctx.isAdmin) {
                //Executed only if it is admin
                user.configureAdminRole(ctx);
                //publishing APIs / subscribing APIs / consumer key and consumer secret
                var properties = apimgr.publishEMMAPIs();
                user.saveOAuthClientKey(parseInt(ctx.tenantId), properties.prodConsumerKey, properties.prodConsumerSecret);
            }

            var tenantId = parseInt(common.getTenantID());
            user.defaultTenantConfiguration(tenantId);
        }
    };

    // return module
    return module;
})();