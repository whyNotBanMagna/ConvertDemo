
#include <cstdio>
#include "lame.h"

class Mp3Encoder {
private:
    FILE* pcmFile;
    FILE* mp3File;
    lame_t lameClient;
public:
    Mp3Encoder();
    ~Mp3Encoder();
    int Init(const char* pcmFilePath,int channels,int bitRate,int sampleRate,const char* mp3FilePath);
    void Encode();
    void Destory();
};
