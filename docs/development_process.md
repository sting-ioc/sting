---
title: Development Process
---
<nav class="page-toc">

<!-- toc -->

- [Publishing](#publishing)
  * [Publishing the Website](#publishing-the-website)

<!-- tocstop -->

</nav>

## Publishing

### Publishing the Website

The website is published by running `bundle exec buildr site:deploy` with credentials that can push to
the `sting-ioc.github.io` repository.

Firstly you create the key via the following command.

    $ ssh-keygen -t rsa -b 4096 -C "peter@realityforge.org" -f ../deploy -P ""

This is a private key and should NOT be checked into source code repository.

Finally you add the public part of the deploy key to the repository at
[https://github.com/sting-ioc/sting-ioc.github.io/settings/keys](https://github.com/sting-ioc/sting-ioc.github.io/settings/keys) and
make sure you give the key write access.
