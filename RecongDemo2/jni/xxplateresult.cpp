#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "xxplateresult.h"

	char res[1024];
	int Init(char *license)
	{
		memset(res, 0, sizeof(res));
		printf("init ok!");
		return 0;//0 success
	}

	int RecordFile(char *jpgfile, char **result, int count)
	{
		memset(res, 0, sizeof(res));
		sprintf(res, "black|123ABC");
		*result = res;
		return 0;
	}

	int RecordStream(char *stream, char **result, int count)
	{
		memset(res, 0, sizeof(res));
		sprintf(res, "red|123ABC");
		*result = res;
		return 0;
	}

	int FInit()//release resources
	{
		memset(res, 0, sizeof(res));
		return 0;
	}

