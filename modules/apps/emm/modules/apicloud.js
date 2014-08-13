var apimgr = (function () {

    var log = new Log();
    var dataConfig = require('/config/emm.js').config();
    var carbon = require('carbon');
    var driver;
    var db;
    var sqlscripts;

    var module = function (dbs) {
        db = dbs;
        driver = require('driver').driver(db);
        sqlscripts = require('/sqlscripts/db.js');
    };

    function mergeRecursive(obj1, obj2) {
        for (var p in obj2) {
            try {
                // Property in destination object set; update its value.
                if (obj2[p].constructor == Object) {
                    obj1[p] = MergeRecursive(obj1[p], obj2[p]);
                } else {
                    obj1[p] = obj2[p];
                }
            } catch (e) {
                // Property in destination object not set; create it and set its
                // value.
                obj1[p] = obj2[p];
            }
        }
        return obj1;
    }

    module.prototype = {
        constructor: module,
        login: function (serviceURL) {

            var params = {};
            params.action = "login";
            params.username = dataConfig.apiManagerConfigurations.username;
            params.password = dataConfig.apiManagerConfigurations.password;

            var url = serviceURL + '/site/blocks/user/login/ajax/login.jag';

            var headers = {};
            log.info(serviceURL);
            var result = post(url, params, headers, null);
            var cookie = result.xhr.getResponseHeader("Set-Cookie");

            return cookie;
        },
        publishAPIs: function (apiInfo, serviceURL, cookie) {

            var params = {};
            params.action = "addAPI";
            params.name = apiInfo.name;
            params.context = apiInfo.context;
            params.version = dataConfig.apiManagerConfigurations.apiVersion;
            params.tier = "Unlimited";
            params.transports = "http&http_checked=http&transports=https&https_checked=https";
            params.description = apiInfo.description;
            params.visibility = "public API";
            params.tags = "emm,mobile";
            params.resourceCount = "0";
            params.subscriptions = "all_tenants";
            params.subscriptionAvailability = "";
            params["resourceMethod-0"] = apiInfo.method;
            params["resourceMethodAuthType-0"] = apiInfo.security;
            params["uriTemplate-0"] = "/*";
            params["resourceMethodThrottlingTier-0"] = "Unlimited";
            params.tiersCollection = "Unlimited";
            var endpoint = dataConfig.apiManagerConfigurations.emmURL + apiInfo.context ;
            var endpoint_config = {};
            endpoint_config.production_endpoints = {};
            endpoint_config.production_endpoints.url = endpoint;
            endpoint_config.production_endpoints.config = "";
            endpoint_config.endpoint_type = "http";
            params.endpoint_config = stringify(endpoint_config);
            var headers = {};
            headers.Cookie = cookie;

            var url = serviceURL + '/site/blocks/item-add/ajax/add.jag';
            var result = post(url, params, headers, null);
        },
        promote: function (apiInfo, serviceURL, cookie, provider) {

            var params = {};
            params.action = "updateStatus";
            params.name = apiInfo.name;
            params.version = dataConfig.apiManagerConfigurations.apiVersion;
            params.provider = provider;
            params.status = "PUBLISHED";
            params.publishToGateway = "true";
            params.requireResubscription = "true";

            var headers = {};
            headers.Cookie = cookie;

            var url = serviceURL + '/site/blocks/life-cycles/ajax/life-cycles.jag';
            var result = post(url, params, headers, null);
        },
        addApplication: function (appInfo, serviceURL, cookie) {
            var params = {};
            params.action = "addApplication";
            params.application = appInfo.name;
            params.tier = "Unlimited";
            params.description = appInfo.description;
            params.callbackUrl = "";
            var headers = {};
            headers.Cookie = cookie;
            var url = serviceURL + '/site/blocks/application/application-add/ajax/application-add.jag';
            var result = post(url, params, headers, null);
            if (result.error) {
                return false;
            } else {
                return true;
            }
        },
        addSubscription: function (apiInfo, serviceURL, cookie, provider, appId) {

            var params = {};
            params.action = "addAPISubscription";
            params.name = apiInfo.name;
            params.version = dataConfig.apiManagerConfigurations.apiVersion;
            params.provider = provider;
            params.tier = "Unlimited";
            params.applicationName = appId;

            var headers = {};
            headers.Cookie = cookie;

            var url = serviceURL + '/site/blocks/subscription/subscription-add/ajax/subscription-add.jag';
            var result = post(url, params, headers, null);
        },
        generateApplicationKey: function (keytype, serviceURL, cookie, appName) {

            var params = {};
            params.action = "generateApplicationKey";
            params.application = appName;
            params.authorizedDomains = "ALL";
            params.callbackUrl = "";
            params.keytype = keytype;
            params.validityTime = 3600;

            var headers = {};
            headers.Cookie = cookie;

            var url = serviceURL + '/site/blocks/subscription/subscription-add/ajax/subscription-add.jag';
            var result = post(url, params, headers, null);

            return result;
        },
        getConsumerKeyPair: function (serviceURL, cookie) {

            var params = {};
            params.action = "getAllSubscriptions";

            var headers = {};
            headers.Cookie = cookie;

            var url = serviceURL + '/site/blocks/subscription/subscription-list/ajax/subscription-list.jag';
            var result = post(url, params, headers, null);

            return result;
        },
        initAPISubscription: function (tenantInfo) {
            //check for super tenant
            if (tenantInfo.tenantId == "-1234") {
                this.publishEMMAPIs();
            }
            return this.subscribeEMMAPIs(tenantInfo);
        },
        getAPIList:function(){
            var allAPIs = new Array();
            allAPIs.push({name: "sender_id", context: "/emm/api/devices/sender_id", method: "GET", description: "Get sender id", security: "Application & Application User"});
            allAPIs.push({name: "isregistered", context: "/emm/api/devices/isregistered", method: "POST", description: "Device is registered?", security: "Application & Application User"});
            allAPIs.push({name: "license", context: "/emm/api/devices/license", method: "GET", description: "Get license.", security: "Application & Application User"});
            allAPIs.push({name: "register", context: "/emm/api/devices/register", method: "POST", description: "Register device.", security: "Application & Application User"});
            allAPIs.push({name: "unregister", context: "/emm/api/devices/unregister", method: "POST", description: "Unregister device", security: "Application & Application User"});
            allAPIs.push({name: "pendingOperations", context: "/emm/api/notifications/pendingOperations", method: "POST", description: "Get pending operations.", security: "Application & Application User"});
            allAPIs.push({name: "clientkey", context: "/emm/api/devices/clientkey", method: "POST", description: "Get client keys.", security: "None"});

            allAPIs.push({name: "isregisterios", context: "/emm/api/devices/devices/isregisteredios", method: "POST", description: "Register iOS", security: "None"});
            allAPIs.push({name: "unregisterios", context: "/emm/api/devices/unregisterios", method: "POST", description: "Un register ios", security: "None"});
            allAPIs.push({name: "pushtoken", context: "/emm/api/devices/pushtoken", method: "POST", description: "Pushtoken ios", security: "None"});
            allAPIs.push({name: "location", context: "/emm/api/devices/location", method: "POST", description: "Get location.", security: "None"});
            allAPIs.push({name: "usersauthenticate", context: "/emm/api/users/authenticate", method: "POST", description: "Authenticate user", security: "None"});


            return allAPIs;
        },
        subscribeEMMAPIs: function (tenantInfo) {
            var storeServiceURL = dataConfig.apiManagerConfigurations.storeServiceURL;
            var appName = tenantInfo.domain;
            var appDescription = "API subscription app for tenant " + tenantInfo.domain;
            var cookie = this.login(storeServiceURL);
            var result = this.addApplication({"name": appName, "description": appDescription}, storeServiceURL, cookie);
            log.info("Added application for API subscription");
            var allAPIs = this.getAPIList();
            for (var i = 0; i < allAPIs.length; i++) {
                this.addSubscription(allAPIs[i], storeServiceURL, cookie,
                    dataConfig.apiManagerConfigurations.username, appName);
            }

            this.generateApplicationKey("PRODUCTION", storeServiceURL, cookie, appName);
            //this.generateApplicationKey("SANDBOX", storeServiceURL, cookie, appName);
            var result = this.getConsumerKeyPair(storeServiceURL, cookie);
            if (result != null) {
                var data = result.data;
                data = parse(data);
                if (data != null) {
                    var subscriptions = data["subscriptions"];
                    if (subscriptions != null && subscriptions != undefined && subscriptions.length > 0) {

                        var subscription = subscriptions[0];
                        var prodConsumerKey = subscription["prodConsumerKey"];
                        var prodConsumerSecret = subscription["prodConsumerSecret"];
                        var sandboxConsumerKey = subscription["sandboxConsumerKey"];
                        var sandboxConsumerSecret = subscription["sandboxConsumerSecret"];

                        var properties = {};
                        properties.prodConsumerKey = prodConsumerKey;
                        properties.prodConsumerSecret = prodConsumerSecret;
                        properties.sandboxConsumerKey = sandboxConsumerKey;
                        properties.sandboxConsumerSecret = sandboxConsumerSecret;
                        return properties;
                    }
                }
            }
        },
        publishEMMAPIs: function () {
            var publisherServiceURL = dataConfig.apiManagerConfigurations.publisherServiceURL;
            var cookie = this.login(publisherServiceURL);
            var allAPIs = this.getAPIList();
            for (var i = 0; i < allAPIs.length; i++) {
                this.publishAPIs(allAPIs[i], publisherServiceURL, cookie);
                this.promote(allAPIs[i], publisherServiceURL, cookie, dataConfig.apiManagerConfigurations.username);
            }
        }
    }

    return module;
})();
