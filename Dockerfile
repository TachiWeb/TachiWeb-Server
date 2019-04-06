FROM ubuntu
RUN apt-get update && apt-get install -y \
nodejs \
openjdk-8-jdk \
gcc-multilib \
g++-multilib \
rpm \
bsdtar \
jq \
rsync \
npm \
yarn \
curl \
zip \
maven \
git
WORKDIR /src
ADD . .
RUN ./scripts/build.sh
