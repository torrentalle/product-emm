var apimgr = (function () {

    var log = new Log();
    var dataConfig = require('/config/emm.js').config();
    var carbon = require('carbon');
    var driver;
    var db;
    var sqlScripts;

    //constants
    const ADD_API_ACTION = "addAPI";
    const ADD_APPLICATION_ACTION = "addApplication";
    const UPDATE_API_STATUS_ACTION = "updateStatus";
    const ADD_API_SUBSCRIPTION_ACTION = "addAPISubscription";
    const GENERATE_APPLICATION_KEY_ACTION = "generateApplicationKey";
    const GET_ALL_SUBSCRIPTIONS_ACTION = "getAllSubscriptions";

    const DEFAULT_API_TRANSPORT = "http&http_checked=http&transports=https&https_checked=https";
    const DEFAULT_ENDPOINT_TYPE = "http";
    const PUBLISHED_API_STATUS = "PUBLISHED";
    const DEFAULT_TIER = "Unlimited";
    const PROD_APPLICATION_KEY_TYPE = "PRODUCTION";
    const PROD_CONSUMER_KEY_PROP = "prodConsumerKey";
    const PROD_CONSUMER_SECRET_PROP = "prodConsumerSecret";
    const SANDBOX_CONSUMER_KEY_PROP = "sandboxConsumerKey";
    const SANDBOX_CONSUMER_SECRET_PROP = "sandboxConsumerSecret";

    //Endpoints
    const ADD_API_ENDPOINT = '/site/blocks/item-add/ajax/add.jag';
    const API_STATE_CHANGE_ENDPOINT = '/site/blocks/life-cycles/ajax/life-cycles.jag';
    const ADD_APPLICATION_ENDPOINT = '/site/blocks/application/application-add/ajax/application-add.jag';
    const ADD_SUBSCRIPTION_ENDPOINT = '/site/blocks/subscription/subscription-add/ajax/subscription-add.jag';
    const SUBSCRIPTION_LIST_ENDPOINT = '/site/blocks/subscription/subscription-list/ajax/subscription-list.jag';

    var module = function (dbs) {
        db = dbs;
        driver = require('driver').driver(db);
        sqlScripts = require('/sqlscripts/db.js');
    };

    module.prototype = {
        constructor: module,
        login: function (serviceURL) {

            var params = {};
            params.action = "login";
            params.username = dataConfig.apiManagerConfigurations.username;
            params.password = dataConfig.apiManagerConfigurations.password;

            var url = serviceURL + '/site/blocks/user/login/ajax/login.jag';
            log.info(serviceURL);
            var result = post(url, params, {}, null);
            return result.xhr.getResponseHeader("Set-Cookie");
        },
        publishAPI: function (apiInfo, serviceURL, cookie) {

            var params = {};
            params.action = ADD_API_ACTION;
            params.name = apiInfo.name;
            params.context = apiInfo.context;
            params.version = dataConfig.apiManagerConfigurations.apiVersion;
            params.tier = apiInfo.tier;
            params.transports = DEFAULT_API_TRANSPORT;
            params.description = apiInfo.description;
            params.visibility = apiInfo.visibility;
            params.tags = apiInfo.tags;
            params.resourceCount = "0";
            params.subscriptions = apiInfo.subscriptions;
            params.subscriptionAvailability = "";
            params["resourceMethod-0"] = apiInfo.method;
            params["resourceMethodAuthType-0"] = apiInfo.security;
            params["uriTemplate-0"] = "/*";
            params["resourceMethodThrottlingTier-0"] = "Unlimited";
            params.tiersCollection = apiInfo.tiersCollection;
            var endpoint = dataConfig.apiManagerConfigurations.emmURL + apiInfo.context;
            var endpoint_config = {};
            endpoint_config.production_endpoints = {};
            endpoint_config.production_endpoints.url = endpoint;
            endpoint_config.production_endpoints.config = "";
            endpoint_config.endpoint_type = DEFAULT_ENDPOINT_TYPE;
            params.endpoint_config = stringify(endpoint_config);
            var headers = {};
            headers.Cookie = cookie;

            var url = serviceURL + ADD_API_ENDPOINT;
            post(url, params, headers, null);
        },
        promote: function (apiInfo, serviceURL, cookie, provider) {
            var params = {};
            params.action = UPDATE_API_STATUS_ACTION;
            params.name = apiInfo.name;
            params.version = dataConfig.apiManagerConfigurations.apiVersion;
            params.provider = provider;
            params.status = PUBLISHED_API_STATUS;
            params.publishToGateway = "true";
            params.requireResubscription = "true";

            var headers = {};
            headers.Cookie = cookie;
            var url = serviceURL + API_STATE_CHANGE_ENDPOINT;
            post(url, params, headers, null);
        },
        addApplication: function (appInfo, serviceURL, cookie) {
            var params = {};
            params.action = ADD_APPLICATION_ACTION;
            params.application = appInfo.name;
            params.tier = DEFAULT_TIER;
            params.description = appInfo.description;
            params.callbackUrl = "";
            var headers = {};
            headers.Cookie = cookie;
            var url = serviceURL + ADD_APPLICATION_ENDPOINT;
            var result = post(url, params, headers, null);
            if (result.error) {
                return false;
            } else {
                return true;
            }
        },
        addSubscription: function (apiInfo, serviceURL, cookie, provider, appId) {

            var params = {};
            params.action = ADD_API_SUBSCRIPTION_ACTION;
            params.name = apiInfo.name;
            params.version = dataConfig.apiManagerConfigurations.apiVersion;
            params.provider = provider;
            params.tier = DEFAULT_TIER;
            params.applicationName = appId;

            var headers = {};
            headers.Cookie = cookie;

            var url = serviceURL + ADD_SUBSCRIPTION_ENDPOINT;
            var result = post(url, params, headers, null);
        },
        generateApplicationKey: function (keytype, serviceURL, cookie, appName) {

            var params = {};
            params.action = GENERATE_APPLICATION_KEY_ACTION;
            params.application = appName;
            params.authorizedDomains = "ALL";
            params.callbackUrl = "";
            params.keytype = keytype;
            params.validityTime = 3600;

            var headers = {};
            headers.Cookie = cookie;

            var url = serviceURL + ADD_SUBSCRIPTION_ENDPOINT;
            var result = post(url, params, headers, null);

            return result;
        },
        getConsumerKeyPair: function (serviceURL, cookie) {

            var params = {};
            params.action = GET_ALL_SUBSCRIPTIONS_ACTION;

            var headers = {};
            headers.Cookie = cookie;

            var url = serviceURL + SUBSCRIPTION_LIST_ENDPOINT;
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
        subscribeEMMAPIs: function (tenantInfo) {
            var storeServiceURL = dataConfig.apiManagerConfigurations.storeServiceURL;
            var appName = tenantInfo.domain;
            var appDescription = "API subscription app for tenant " + tenantInfo.domain;
            var cookie = this.login(storeServiceURL);
            this.addApplication({"name": appName, "description": appDescription}, storeServiceURL, cookie);
            log.info("Added application "+appName+" for API subscription");
            var allAPIs = dataConfig.apiManagerConfigurations.deviceAPIs;
            for (var i = 0; i < allAPIs.length; i++) {
                this.addSubscription(allAPIs[i], storeServiceURL, cookie,
                    dataConfig.apiManagerConfigurations.username, appName);
            }

            this.generateApplicationKey(PROD_APPLICATION_KEY_TYPE, storeServiceURL, cookie, appName);
            var result = this.getConsumerKeyPair(storeServiceURL, cookie);
            if (result != null) {
                var data = result.data;
                data = parse(data);
                if (data != null) {
                    var subscriptions = data["subscriptions"];
                    if (subscriptions != null && subscriptions != undefined && subscriptions.length > 0) {
                        var subscription = subscriptions[0];
                        var properties = {};
                        properties.prodConsumerKey = subscription[PROD_CONSUMER_KEY_PROP];
                        properties.prodConsumerSecret = subscription[PROD_CONSUMER_SECRET_PROP];
                        properties.sandboxConsumerKey = subscription[SANDBOX_CONSUMER_KEY_PROP];
                        properties.sandboxConsumerSecret = subscription[SANDBOX_CONSUMER_SECRET_PROP];
                        return properties;
                    }
                }
            }
        },
        publishEMMAPIs: function () {
            var publisherServiceURL = dataConfig.apiManagerConfigurations.publisherServiceURL;
            var cookie = this.login(publisherServiceURL);
            var allAPIs = dataConfig.apiManagerConfigurations.deviceAPIs;
            for (var i = 0; i < allAPIs.length; i++) {
                this.publishAPI(allAPIs[i], publisherServiceURL, cookie);
                this.promote(allAPIs[i], publisherServiceURL, cookie, dataConfig.apiManagerConfigurations.username);
            }
        }
    }

    return module;
})();
