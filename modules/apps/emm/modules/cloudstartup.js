var startup = (function () {

    var log = new Log();
    var db;
    var driver, user, apiMgr;
    var userModule = require('/modules/user.js').user;
    var apiMgrModule = require("/modules/apicloud.js").apimgr;
    var carbon = require('carbon');

    var module = function (dbs) {
        db = dbs;
        driver = require('driver').driver(db);
        user = new userModule(db);
        apiMgr = new apiMgrModule(db);
    };

    // prototype
    module.prototype = {
        constructor: module,

        //this executes after user logged in
        onUserLogin: function (ctx) {
            if (ctx.isAdmin) {
                //Executed only if it is admin
                user.configureAdminRole(ctx);

                //publishing APIs / subscribing APIs / consumer key and consumer secret
                var tenantInfo = {};
                tenantInfo.tenantId = ctx.tenantId;
                tenantInfo.domain = carbon.server.tenantDomain(ctx);
                var oAuthClientKey = user.getOAuthClientKey(parseInt(ctx.tenantId));
                if (!oAuthClientKey) {
                    var properties = apiMgr.initAPISubscription(tenantInfo);
                    log.debug("API consumer keys " + stringify(properties));
                    if (properties.prodConsumerKey && properties.prodConsumerSecret) {
                        user.saveOAuthClientKey(parseInt(ctx.tenantId), properties.prodConsumerKey, properties.prodConsumerSecret);
                    } else {
                        log.error("Error in getting Consumer key & secret.");
                    }
                }
            }

            var tenantId = parseInt(ctx.tenantId);
            user.defaultTenantConfiguration(tenantId);
        }
    };
    return module;
})();