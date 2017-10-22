# dotenv

A Clojure library designed to load environment variables.

It loads them from (by order of priority):

* Application environment specific .env file (defaults to .env.development)
* .env file
* Properties
* System Environment Variables


This was born out of the frustration of using existing libraries and from the fact that I find it less than ideal to introduce a new standard regarding env files.


## Install

Add the dependency to you `project.clj`:

```clojure
:dependencies [[lynxeyes/dotenv "1.0.0"]]
```

and `lein deps`!

## Usage

Dotenv was built to supply 2 major pieces of information:

* app-env - a String with the currently running app env (development, production, test). Defaults to "development".
* env - a function to access the environment vars map

To use it, require it wherever you need it:

```clojure
(require '[dotenv :refer [env app-env]])
```

Now, `app-env` will tell you which environment is your app running on.

```clojure
=> app-env
"development"
```

And `env` will allow you to refer to environment variables both by String or Keyword:

```clojure
=> (env "SOME_ENV_VAR")
"whatever we set on the var"
```

or

```clojure
=> (env :SOME_ENV_VAR)
"whatever we set on the var"
```

You may also pass no arguments to `env` and get the full environment variable map back:

```clojure
=> (env)
{"SOME_VAR" "some value" ....too big for me to actually represent here..sorry.... }
```


## License

Copyright © 2017 Ivo Jesus

Distributed under the MIT Public Licence.
Use it as you will. Contribute if you have the time.
