#!/bin/bash
# build matrices classes for other basic java types (namely int, long and double)
# from the float implementation

# LIMITATIONS: when writing explicit numbers, use explicit cast instead of
# special notations. Example
#   10.5f => (float) 10.5
# also, be careful when designing tests
#
# KNOWN PROBLEMS:
# Random.nextFloat() gets converted in nextInteger() instead of nextInt()

function usage() {
    echo "USAGE: $0 TYPE WRAPPER_TYPE"
    echo "creates a new implementation of sparse matrices of TYPE, in the _TYPE"
    echo "directory."
    echo "TYPE can be 'int', 'long', 'float' or 'double'"
    echo "'short' and 'byte' are also accepted, but beware!"
    echo "short and byte need explicit casts for multiplications"
}

if [ "$#" -lt 1 -o "$1" == "--help" -o "$1" == "-h" ]
then
    usage
    exit 1
fi

source_type='float'
source_type_wrapper='Float'

target_type="$1"
case "$target_type" in
"int"       ) target_type_wrapper="Integer";;
"long"      ) target_type_wrapper="Long";;
"float"     ) target_type_wrapper="Float";;
"double"    ) target_type_wrapper="Double";;
"short"     ) target_type_wrapper="Short";;
"byte"      ) target_type_wrapper="Byte";;
*   ) echo "ERROR: unrecognized type $target_type"; usage; exit -1;;
esac

if [ -e "$target_type""_" -a ! -d "$target_type""_" ]
then 
   echo "ERROR: $target_type""_ already exists and is not a directory."
   exit 2
fi

if [ -d "$target_type""_" ]
then
    read -p "$target_type""_ already exists. Overwrite? [y/N] " ans
    if [ "$ans" != "y" ]
    then 
        echo "Exiting."
        exit 2
    fi
fi

find float_ | grep -v '/\..*' | # ignore hidden files
while read path
do
    copy_name=$(echo "$path" | sed "s/$source_type/$target_type/g")
    if [ -d "$path" ]
    then
        mkdir -p "$copy_name"
    else
        # copy file. Add auto-generated header when appropriate
        case "$copy_name" in
        *.java ) 

            if [ "$target_type" != "$source_type" ]
            then
                echo -n "" > "$copy_name" # clean file
                echo "//" >> "$copy_name"
                echo "// Auto-generated file from $path" >> "$copy_name"
                echo "//" >> "$copy_name"

                cat "$path" | sed -e "s/$source_type/$target_type/g" \
                    -e "s/$source_type_wrapper/$target_type_wrapper/g" \
                    >> "$copy_name"
            else
                # do not alter original
                cp "$path" "$copy_name"
            fi

            ;;

        * ) 
            echo "WARNING: non-java file $path. Copying as-is."
            cp "$path" "$copy_name"
            ;;
        esac
    fi
done

