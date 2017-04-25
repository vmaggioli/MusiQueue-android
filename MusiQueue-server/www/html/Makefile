CSS_TARGETS := $(patsubst %.scss,%.css,$(wildcard web/styles/*.scss))

all: $(CSS_TARGETS)

web/styles/%.css: web/styles/%.scss
	sass $? $@
