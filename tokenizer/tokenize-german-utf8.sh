#!/bin/sh

export SRC_DIR=$(cd "$(dirname "$0")/"; pwd)

CMD=${SRC_DIR}/cmd
PARAM=${SRC_DIR}/param/utf8-german

${CMD}/filter-german-tokin-utf8.perl $* |
${CMD}/punctuation-cutoff-utf8.perl |
${CMD}/add-missing-blanks-utf8.perl |
${CMD}/filter-german-tokin2-utf8.perl |
${CMD}/disamb-period-utf8.perl -a -f ${PARAM} |
${CMD}/disamb-num-period-utf8.perl -f ${PARAM}.num |
${CMD}/s-markup-utf8.perl -s ".\!?" -p '<p>' |
${CMD}/filter-german-tokout-utf8.perl
