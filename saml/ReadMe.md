[Demo](https://www.youtube.com/watch?v=4VoxcHhj77E)

* This is a POC Spring Boot Security application which implements Authentication via SAML by securing a REST end-point (/matrices).

* /matrices REST end-point is exposed by flask (python based micro web-framework).
That project is accessible at [test_matrices](https://github.com/maliamir/python/tree/master/test-matrices).

* [okta](https://www.okta.com/) has been used an Identity Provider (IDP).

    This project has been implemented from [okta instructions](https://developer.okta.com/blog/2017/03/16/spring-boot-saml).


Since, project has already been implemented by following instructions, you just need to setup your own okta IDP account and update "saml/src/java/main/resources/application.properties".

Upon running the application, hit:
https://localhost:8443

If you’re using Chrome, you’ll likely see a privacy error. Connection Not Private. Click the “ADVANCED” link at the bottom. Then click the “proceed to localhost (unsafe)” link.

Next, you’ll be redirected to Okta to sign in, once signed-in successfully it will return the content for /matrices REST end-point as exposed by that flask application.
