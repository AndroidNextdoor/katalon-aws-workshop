FROM katalonstudio/katalon:8.1.0
MAINTAINER <MAINTAINER EMAIL>

RUN apt-get update \
  && apt-get install -y \
    curl \
    unzip

RUN curl https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip -o awscliv2.zip \
  && curl -sSfL https://raw.githubusercontent.com/anchore/grype/main/install.sh | sh -s -- -b /usr/local/bin \
  && unzip awscliv2.zip \
  && ./aws/install \
  && rm -rf aws awscliv2.zip

RUN mkdir "/root/.aws" \
    && touch "/root/.aws/config" \
    && touch "/root/.aws/credentials"

WORKDIR /katalon/katalon

RUN grype dir:/ --only-fixed --add-cpes-if-none --file ".grype_vulnerability_report"
