#/bin/bash

range_start=${1:-1}
range_stop=${2:-100}
sleeptime=${3:-0}

echo "generating data..."

for((number=${range_start};number<=${range_stop};number++))
{
	echo "loop ${number} of ${range_stop}"
	if [ -z "${4}" ]
	then
		./generate_data.sh
	else
		./generate_data.sh >> "${4}"
		if [ ${number} -lt ${range_stop} ]
		then
			echo "waiting for ${sleeptime}"
			sleep "${sleeptime}"
		fi
	fi	
}
echo "end of generating data."
