#!/system/bin/sh
. ./sdcard/onixheat/iptables.sh
GREEN='\033[0;32m'
NC='\033[0m'
IP6TABLES=/system/bin/ip6tables
IPTABLES=/system/bin/iptables

banner(){
cat <<EOF
░█████╗░███╗░░██╗██╗
██╔══██╗████╗░██║██║
██║░░██║██╔██╗██║██║
██║░░██║██║╚████║██║
╚█████╔╝██║░╚███║██║
░╚════╝░╚═╝░░╚══╝╚═╝

██╗░░██╗██╗░░██╗███████╗░█████╗░████████╗
╚██╗██╔╝██║░░██║██╔════╝██╔══██╗╚══██╔══╝
░╚███╔╝░███████║█████╗░░███████║░░░██║░░░
░██╔██╗░██╔══██║██╔══╝░░██╔══██║░░░██║░░░
██╔╝╚██╗██║░░██║███████╗██║░░██║░░░██║░░░
╚═╝░░╚═╝╚═╝░░╚═╝╚══════╝╚═╝░░╚═╝░░░╚═╝░░░
EOF
echo "${GREEN}http://facebook.com/friyadhibiermann/${NC}"
}
run(){
/system/bin/dnscrypt-proxy -config /data/media/0/onixheat/dnscrypt-proxy.toml > /dev/null 2>&1 &
}
stop(){
pkill dnscrypt-proxy
}
#redirect
#pubg
#resetroute
#banner
case $1 in 
  "connect")
    run
    redirect
    if [ $2 == "pubg" ];then
       pubg
       echo "PUBG BYPASS MODE"
       banner
    else
       echo "PROXY BYPASS INTERNET (IM AnonymousID)"
       banner
    fi
    
    echo "
█▀▀ █▀█ █▄░█ █▄░█ █▀▀ █▀▀ ▀█▀ █▀▀ █▀▄
█▄▄ █▄█ █░▀█ █░▀█ ██▄ █▄▄ ░█░ ██▄ █▄▀"
  ;;
  "disconnect")
    stop
    resetroute
    banner
    echo "
█▀▄ █ █▀ █▀▀ █▀█ █▄░█ █▀▀ █▀▀ ▀█▀ █▀▀ █▀▄
█▄▀ █ ▄█ █▄▄ █▄█ █░▀█ ██▄ █▄▄ ░█░ ██▄ █▄▀"
  ;;
esac
