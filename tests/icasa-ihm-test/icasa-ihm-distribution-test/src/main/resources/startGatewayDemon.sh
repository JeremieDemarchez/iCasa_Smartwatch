#!/bin/bash
ICASA_COMMAND="java -Dgosh.args=--noi -jar bin/felix.jar"
nohup bash -c "${ICASA_COMMAND}  &"
