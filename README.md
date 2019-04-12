# stress-mysql-server

run queries readed from general query logger at the same speed or mode brute force

# setup folder tree for an execution

creare una workin directory come la seguente:

mkdir /test1
mkdir /test1/bin
mkdir /test1/load-data
mkdir /test1/files

per avviare la procedura servono:

	1) general query log
	2) bin log
	3) mysql-stress-tool-pre1.jar
	3) mysql-stress-tool-pre2.jar
	3) mysql-stress-tool.jar

la prima cosa da fare è rendere il bin log 'leggibile'.
per far questo è necessario avviare per ogni bin log i passi:

	1) mysqlbinlog --verbose bin.009748 > bin.009748.txt (questo produce sia il file bin.009748.txt nella dir corrente, che una lista di file in /tmp)
	2) mv bin.009748.txt /test1/bin
	3) mv /tmp/SQL* /test1/load-data


