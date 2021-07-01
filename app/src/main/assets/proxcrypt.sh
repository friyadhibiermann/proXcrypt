#!/system/bin/sh
GREEN='\033[0;32m'
NC='\033[0m'
IP6TABLES=/system/bin/ip6tables
IPTABLES=/system/bin/iptables
redirect(){

$IPTABLES -F
$IPTABLES -t nat -F
$IPTABLES -t nat -X

# Force a specific DNS
# First two lines delete current DNS settings
$IPTABLES -t nat -D OUTPUT -p tcp ! -d 1.1.1.1 --dport 53 -j DNAT --to-destination 127.0.0.1:5354
$IPTABLES -t nat -D OUTPUT -p udp ! -d 1.1.1.1 --dport 53 -j DNAT --to-destination 127.0.0.1:5354

# These two new lines sets DNS running at 127.0.0.1 on port 5354
$IPTABLES -t nat -A OUTPUT -p tcp ! -d 1.1.1.1 --dport 53 -j DNAT --to-destination 127.0.0.1:5354
$IPTABLES -t nat -A OUTPUT -p udp ! -d 1.1.1.1 --dport 53 -j DNAT --to-destination 127.0.0.1:5354
}
pubg(){
#PUBG
$IPTABLES -A INPUT -p tcp --destination-port 17500 -j DROP
$IPTABLES -A INPUT -p tcp --destination-port 443 -j DROP
$IPTABLES -A INPUT -p tcp --destination-port 80 -j DROP
$IPTABLES -A INPUT -p tcp --destination-port 8080 -j DROP
$IPTABLESs -A INPUT -p tcp --destination-port 8082 -j DROP

iptables -I OUTPUT -p tcp --dport http-alt -j DROP
iptables -I OUTPUT -p tcp --dport 10012 -j DROP
iptables -I OUTPUT -p tcp --dport 20371 -j DROP
iptables -I OUTPUT -p tcp --dport 15692 -j DROP
iptables -I OUTPUT -p tcp --dport 18081 -j DROP
iptables -I OUTPUT -p tcp --dport 8085 -j DROP
iptables -I OUTPUT -p tcp --dport https -j DROP
iptables -I OUTPUT -p tcp --dport 49514 -j DROP
iptables -I OUTPUT -p tcp --dport 8086 -j DROP

iptables -I INPUT -p tcp --dport http-alt -j DROP
iptables -I INPUT -p tcp --dport 10012 -j DROP
iptables -I INPUT -p tcp --dport 20371 -j DROP
iptables -I INPUT -p tcp --dport 15692 -j DROP
iptables -I INPUT -p tcp --dport 18081 -j DROP
iptables -I INPUT -p tcp --dport 8085 -j DROP
iptables -I INPUT -p tcp --dport https -j DROP
iptables -I INPUT -p tcp --dport 49514 -j DROP
iptables -I INPUT -p tcp --dport 8086 -j DROP
iptables -I INPUT -p tcp --dport 17500 -j DROP
}

resetroute(){
iptables -F
iptables -X
iptables -t nat -F
iptables -t nat -X
iptables -t mangle -F
iptables -t mangle -X
iptables -P INPUT ACCEPT
iptables -P FORWARD ACCEPT
iptables -I INPUT -j ACCEPT
iptables -P OUTPUT ACCEPT
iptables -A INPUT -i lo -j ACCEPT -m comment --comment "Allow all loopback traffic"
iptables -A INPUT ! -i lo -d 127.0.0.0/8 -j REJECT -m comment --comment "Drop all traffic to 127 that doesn't use lo"
iptables -A OUTPUT -j ACCEPT -m comment --comment "Accept all outgoing"
iptables -A INPUT -j ACCEPT -m comment --comment "Accept all incoming"
iptables -A INPUT -m state --state ESTABLISHED,RELATED -j ACCEPT -m comment --comment "Allow all incoming on established connections"
}

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
./proxcrypt${1} -config setting.toml &
}
stop(){
pkill proxcrypt${$3}
}
#redirect
#pubg
#resetroute
#banner
case $1 in 
  "connect")
    run $3
    redirect
    if [ $2 == 'pubg' ];then
      pubg
      echo "PUBG BYPASS MODE"
      banner
    else
      echo "PROXY BYPASS INTERNET (Annonimous MODE)"
      banner
    fi
    echo "
█▀▀ █▀█ █▄░█ █▄░█ █▀▀ █▀▀ ▀█▀ █▀▀ █▀▄
█▄▄ █▄█ █░▀█ █░▀█ ██▄ █▄▄ ░█░ ██▄ █▄▀"
  ;;
  "disconect")
    stop
    resetroute
    banner
    echo "
█▀▄ █ █▀ █▀▀ █▀█ █▄░█ █▀▀ █▀▀ ▀█▀ █▀▀ █▀▄
█▄▀ █ ▄█ █▄▄ █▄█ █░▀█ ██▄ █▄▄ ░█░ ██▄ █▄▀"
  ;;
esac
