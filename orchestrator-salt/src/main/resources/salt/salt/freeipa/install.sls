freeipa-pkg-install:
  pkg.installed:
    - pkgs:
        - ntp
        - ipa-server
        - ipa-server-dns

net.ipv6.conf.lo.disable_ipv6:
  sysctl.present:
    - value: 0

replace_default_ssl_conf:
  file.managed:
    - name: /etc/nginx/sites-enabled/ssl.conf
    - makedirs: True
    - source: salt://nginx/conf/ssl.conf

restart_nginx_after_default_ssl_reconfig:
  service.running:
    - name: nginx
    - enable: True
    - watch:
        - file: /etc/nginx/sites-enabled/ssl.conf

/opt/salt/scripts/freeipa_install.sh:
  file.managed:
    - makedirs: True
    - user: root
    - group: root
    - mode: 700
    - source: salt://freeipa/scripts/freeipa_install.sh

install-freeipa:
  cmd.run:
    - name: /opt/salt/scripts/freeipa_install.sh && echo $(date +%Y-%m-%d:%H:%M:%S) >> /var/log/freeipa_install-executed
    - env:
      - FPW: {{salt['pillar.get']('freeipa:password')}}
    - unless: test -f /var/log/freeipa_install-executed
    - require:
        - file: /opt/salt/scripts/freeipa_install.sh

