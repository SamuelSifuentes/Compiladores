#!/bin/bash

nasm gerado.txt -g -w-zeroing -f elf64 -o gerado.o
ld gerado.o -o gerado
./gerado
