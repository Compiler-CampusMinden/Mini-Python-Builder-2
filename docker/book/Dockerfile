# see ../Makefile
ARG DEBIAN_RELEASE_NAME

# keep the debian version in sync with the debian version used for the run-container
# to prevent issues with mismatching shared libraries
FROM rust:slim-$DEBIAN_RELEASE_NAME AS rust-builder

ARG MDBOOK_VERSION=0.4

RUN cargo install mdbook \
    --no-default-features --features search \
    --vers "^${MDBOOK_VERSION}" --locked

ARG MDBOOK_PANDOC_VERSION=0.5

RUN cargo install mdbook-pandoc \
    --vers "^${MDBOOK_PANDOC_VERSION}" --locked

FROM debian:$DEBIAN_RELEASE_NAME-slim

COPY --from=rust-builder /usr/local/cargo/bin/mdbook /usr/local/bin
COPY --from=rust-builder /usr/local/cargo/bin/mdbook-pandoc /usr/local/bin

ARG FONT_RECURSIVE_VERSION=1.085
ARG FONT_RECURSIVE=https://github.com/arrowtype/recursive/releases/download/v$FONT_RECURSIVE_VERSION/ArrowType-Recursive-$FONT_RECURSIVE_VERSION.zip

ARG PANDOC_VERSION=2.11.4
# Pandoc binary as .deb -- see https://github.com/jgm/pandoc/releases/latest
ARG PANDOCDEB=https://github.com/jgm/pandoc/releases/download/2.11.4/pandoc-$PANDOC_VERSION-1-amd64.deb

# Install everything
RUN apt-get update \
    # Packages: TeX-Live and fonts
    && apt-get install -y --no-install-recommends \
        apt-utils bash wget make graphviz biber \
        texlive-base texlive-latex-base \
        texlive-latex-recommended texlive-latex-extra \
        texlive-science texlive-pictures \
        texlive-bibtex-extra texlive-lang-english texlive-lang-german \
        texlive-font-utils texlive-fonts-recommended texlive-fonts-extra \
        fontconfig lmodern ghostscript gsfonts cm-super librsvg2-bin \
        texlive-luatex \
    #
    # Plantuml
        plantuml \
    #
    # further fonts
        fonts-noto-color-emoji \
    && wget $FONT_RECURSIVE -O recursive.zip && unzip -d recursive recursive.zip && cp recursive/ArrowType-Recursive-$FONT_RECURSIVE_VERSION/Recursive_Desktop/separate_statics/*/* /usr/local/share/fonts && fc-cache && rm -rf recursive.zip recursive \
    #
    # Pandoc
    && wget $PANDOCDEB && dpkg -i pandoc* && rm pandoc*                     \
    #
    #
    # Avoid running mktexpk in container
    && mktexpk --mfmode / --bdpi 600 --mag 1+0/600 --dpi 600 ecrm0900       \
    && mktexpk --mfmode / --bdpi 600 --mag 1+0/600 --dpi 600 ecrm1728       \
    && mktexpk --mfmode / --bdpi 600 --mag 1+0/600 --dpi 600 ecrm1095       \
    #
    # Clean-Up
    && apt-get autoremove -y && apt-get clean -y && rm -rf /var/lib/apt/lists/*
