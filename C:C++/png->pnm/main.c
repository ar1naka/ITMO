#include "return_codes.h"

#include <stdbool.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#if defined(ZLIB)
#include <zlib.h>
#elif defined(LIBDEFLATE)
#include <libdeflate.h>
#elif defined(ISAL)
#include <include/igzip_lib.h>
#else
#error "Not supported library"
#endif

typedef unsigned char hex;
typedef int ERROR;
ERROR error = SUCCESS;

typedef struct
{
	char *colour;
	int type;
	hex depth;
	size_t width, height;
} header;

typedef struct
{
	size_t size, outSize;
	hex *out;
	hex *res;
} uncompressData;

typedef struct
{
	hex *colour;
	size_t colourSize;
	bool p;
} PLTE;

void checkType(FILE *filePNG)
{
	const hex codes[] = { 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A };
	hex fileCodes[8];
	if (fread(fileCodes, sizeof(hex), 8, filePNG) != 8)
	{
		error = ERROR_DATA_INVALID;
	}
	if (memcmp(codes, fileCodes, 8) != 0)
	{
		error = ERROR_DATA_INVALID;
	}
}

int convert(const hex *value, const int startIndex, const int size)
{
	int result = 0;
	int bitStep = 1;
	for (int i = size + startIndex - 1; i > startIndex - 1; i--, bitStep <<= 8)
	{
		result += ((int)value[i]) << ((size + startIndex - 1 - i) * 8);
	}
	return result;
}

int getChunkLen(FILE *filePNG)
{
	hex *len = malloc(sizeof(hex) * 4);
	if (len == NULL)
	{
		error = ERROR_OUT_OF_MEMORY;
		return -1;
	}
	if (fread(len, sizeof(hex), 4, filePNG) != 4)
	{
		error = ERROR_DATA_INVALID;
		free(len);
		return -1;
	}
	int out = convert(len, 0, 4);
	free(len);
	return out;
}

void typeOfChunk(FILE *inputFile, hex name[])
{
	if (fread(name, sizeof(hex), 4, inputFile) != 4)
	{
		error = ERROR_DATA_INVALID;
		return;
	}
}

void readHeader(FILE *filePNG, header *head, int len)
{
	hex *header = (hex *)malloc(sizeof(hex) * len);
	if (header == NULL)
	{
		error = ERROR_OUT_OF_MEMORY;
		return;
	}
	if (fread(header, sizeof(hex), len, filePNG) != len)
	{
		free(header);
		error = ERROR_DATA_INVALID;
		return;
	}
	head->width = convert(header, 0, 4);
	head->height = convert(header, 4, 4);
	head->depth = header[8];
	if (head->depth != 0x08)
	{
		free(header);
		error = ERROR_DATA_INVALID;
		return;
	}
	switch (header[9])
	{
	case 0x00:
		head->type = 0;
		head->colour = "P5";
		free(header);
		return;
	case 0x02:
		head->type = 2;
		head->colour = "P6";
		free(header);
		return;
	case 0x03:
		head->type = 3;
		head->colour = "P5";
		free(header);
		return;
	default:
		free(header);
		error = ERROR_DATA_INVALID;
		return;
	}
}

int f_uncompress(header *head, uncompressData *data)
{
	size_t size = 0;
	data->out = (hex *)malloc(sizeof(hex) * head->height * (head->width * (head->type + 1) + 1));
	if (data->out == NULL)
	{
		error = ERROR_OUT_OF_MEMORY;
		return error;
	}
	if (head->type == 3)
	{
		size = head->height * (head->width + 1);
	}
	else
	{
		size = head->height * (head->width * (head->type + 1) + 1);
	}
#if defined(ZLIB)
	int check = uncompress(data->out, (uLongf *)&size, data->res, data->size);
	if (check != Z_OK)
	{
		error = ERROR_UNKNOWN;
		return -1;
	}

	data->outSize = size;
#elif defined(LIBDEFLATE)
	struct libdeflate_decompressor *a = libdeflate_alloc_decompressor();
	int check = libdeflate_zlib_decompress(a, data->res, data->size, data->out, size, NULL);
	if (check != LIBDEFLATE_SUCCESS)
	{
		error = ERROR_UNKNOWN;
		libdeflate_free_decompressor(a);
		return -1;
	}
	libdeflate_free_decompressor(a);
	data->outSize = size;
#elif defined(ISAL)
	struct inflate_state a;
	isal_inflate_init(&a);
	a.avail_in = data->size;
	a.next_in = data->res;
	a.avail_out = size;
	a.next_out = data->out;
	a.crc_flag = IGZIP_ZLIB;
	int check = isal_inflate(&a);
	if (check != ISAL_DECOMP_OK)
	{
		error = ERROR_UNKNOWN;
		return -1;
	}
	data->outSize = size;
#endif
	return 0;
}

void readAllIdat(FILE *in, uncompressData *n, header *head, PLTE *plte)
{
	int tmp = 0;
	hex *res = NULL;
	hex *arr = NULL;
	n->res = NULL;
	plte->colourSize = 0;
	while (1)
	{
		int data = getChunkLen(in);
		if (data == -1)
		{
			error = ERROR_DATA_INVALID;
			return;
		}
		int crc = 4;
		hex chunkName[4];
		typeOfChunk(in, chunkName);
		if (chunkName[0] == 0x49 && chunkName[1] == 0x44 && chunkName[2] == 0x41 && chunkName[3] == 0x54)
		{
			arr = (hex *)malloc(sizeof(hex) * data);
			if (arr == NULL)
			{
				error = ERROR_OUT_OF_MEMORY;
				return;
			}
			tmp += data;
			if (fread(arr, sizeof(hex), data, in) != data)
			{
				error = ERROR_DATA_INVALID;
				free(arr);
				return;
			}
			hex *temp = (hex *)realloc(res, tmp);
			if (temp == NULL)
			{
				free(res);
				free(arr);
				error = ERROR_OUT_OF_MEMORY;
				return;
			}
			res = temp;
			for (int i = 0; i < data; i++)
			{
				res[i + tmp - data] = arr[i];
			}
			fseek(in, crc, SEEK_CUR);
		}
		else if (chunkName[0] == 0x49 && chunkName[1] == 0x45 && chunkName[2] == 0x4E && chunkName[3] == 0x44)
		{
			break;
		}
		else if (chunkName[0] == 'P' && chunkName[1] == 'L' && chunkName[2] == 'T' && chunkName[3] == 'E')
		{
			plte->p = true;
			if (head->type == 0 || head->type == 2)
			{
				error = ERROR_DATA_INVALID;
				return;
			}
			else
			{
				plte->colour = (hex *)malloc(sizeof(hex) * data);
				if (plte->colour == NULL)
				{
					return;
				}
				if (fread(plte->colour, sizeof(hex), data, in) != data)
				{
					error = ERROR_DATA_INVALID;
					return;
				}
				plte->colourSize = data;
				if (plte->colourSize % 3 != 0)
				{
					free(plte->colour);
					error = ERROR_DATA_INVALID;
					return;
				}
				fseek(in, crc, SEEK_CUR);
			}
		}
		else
		{
			fseek(in, data + crc, SEEK_CUR);
		}
	}
	free(arr);
	n->size = tmp;
	n->res = (hex *)malloc(sizeof(hex) * n->size);
	if (n->res == NULL)
	{
		free(res);
		error = ERROR_OUT_OF_MEMORY;
		return;
	}
	for (int i = 0; i < n->size; i++)
	{
		n->res[i] = res[i];
	}
	free(res);
}

long PaethPredictor(long a, long b, long c)
{
	long p = a + b - c;
	long pa = labs(p - a);
	long pb = labs(p - b);
	long pc = labs(p - c);

	if (pa <= pb && pa <= pc)
	{
		return a;
	}
	if (pb <= pc)
	{
		return b;
	}
	return c;
}

void subFilter(size_t i, uncompressData *n, header *head)
{
	for (size_t j = 1 + (head->type + 1); j < (head->width * (head->type + 1) + 1); j++)
	{
		n->out[i * (head->width * (head->type + 1) + 1) + j] +=
			n->out[i * (head->width * (head->type + 1) + 1) + j - (head->type + 1)];
	}
}

void upFilter(size_t i, uncompressData *n, header *head)
{
	for (size_t j = 1; j < (head->width * (head->type + 1) + 1); j++)
	{
		n->out[i * (head->width * (head->type + 1) + 1) + j] += n->out[(i - 1) * (head->width * (head->type + 1) + 1) + j];
	}
}

void averageFilter(size_t i, uncompressData *n, header *head)
{
	for (long j = 1; j < (head->width * (head->type + 1) + 1); j++)
	{
		size_t a;
		if (j <= (head->type + 1))
		{
			a = (int)(n->out[(i - 1) * (head->width * (head->type + 1) + 1) + j] / 2);
		}
		else
		{
			a = (int)((n->out[(i - 1) * (head->width * (head->type + 1) + 1) + j] +
					   n->out[i * (head->width * (head->type + 1) + 1) + j - (head->type + 1)]) /
					  2);
		}
		n->out[i * (head->width * (head->type + 1) + 1) + j] += a;
	}
}

void paethFilter(size_t i, uncompressData *n, header *head)
{
	for (long j = 1; j < (head->width * (head->type + 1) + 1); j++)
	{
		long a, b, c;
		if (j <= (head->type + 1))
		{
			a = 0;
			b = n->out[(i - 1) * (head->width * (head->type + 1) + 1) + j];
			c = 0;
		}
		else
		{
			a = n->out[i * (head->width * (head->type + 1) + 1) + j - (head->type + 1)];
			b = n->out[(i - 1) * (head->width * (head->type + 1) + 1) + j];
			c = n->out[(i - 1) * (head->width * (head->type + 1) + 1) + j - (head->type + 1)];
		}
		n->out[i * (head->width * (head->type + 1) + 1) + j] += PaethPredictor(a, b, c);
	}
}

void filter(uncompressData *n, header *head)
{
	for (size_t i = 0; i < head->height; i++)
	{
		long firstByte = n->out[i * (head->width * (head->type + 1) + 1)];
		long type = firstByte & 0x07;
		switch (type)
		{
		case 0:
			// none
			break;
		case 1:
			// sub
			subFilter(i, n, head);
			break;
		case 2:
			// up
			upFilter(i, n, head);
			break;
		case 3:
			// average
			averageFilter(i, n, head);
			break;
		case 4:
			// paeth
			paethFilter(i, n, head);
			break;
		default:
			error = ERROR_DATA_INVALID;
		}
	}
}

void convertResult(header *head, FILE *out, uncompressData *data, PLTE *plte)
{
	if (plte->p == true && plte->colourSize > 0)
	{
		fprintf(out, "%s\n%zu %zu\n%i\n", "P6", head->width, head->height, 255);
		for (int i = 0; i < data->outSize; i++)
		{
			if (i % (head->width + 1))
			{
				fwrite(&plte->colour[data->out[i] * 3], sizeof(hex), 3, out);
			}
		}
	}
	else
	{
		fprintf(out, "%s\n%zu %zu\n%i\n", head->colour, head->width, head->height, 255);
		for (int i = 0; i < head->height; i++)
		{
			fwrite(data->out + (i * (head->width * (head->type + 1) + 1)) + 1, sizeof(hex), (head->width * (head->type + 1) + 1) - 1, out);
		}
	}
}

int main(int argc, char *argv[])
{
	FILE *inputFile;
	if (argc != 3)
	{
		fprintf(stderr, "Not enough arguments");
		return ERROR_PARAMETER_INVALID;
	}

	inputFile = fopen(argv[1], "rb");
	if (inputFile == NULL)
	{
		error = ERROR_CANNOT_OPEN_FILE;
		fprintf(stderr, "wrong input fata");
		return error;
	}

	checkType(inputFile);
	if (error != SUCCESS)
	{
		fprintf(stderr, "wrong type of input file");
		fclose(inputFile);
		return error;
	}

	int len = getChunkLen(inputFile);
	if (error != SUCCESS)
	{
		fclose(inputFile);
		fprintf(stderr, "wrong length of chunk");
		return error;
	}
	hex chunkName[4];
	typeOfChunk(inputFile, chunkName);
	if (error != SUCCESS)
	{
		fclose(inputFile);
		fprintf(stderr, "wrong type of chunk");
		return error;
	}

	header head;
	readHeader(inputFile, &head, len);

	if (error != SUCCESS)
	{
		fclose(inputFile);
		fprintf(stderr, "wrong data in header");
		return error;
	}
	fseek(inputFile, 4, SEEK_CUR);
	uncompressData data;
	PLTE plte;
	readAllIdat(inputFile, &data, &head, &plte);

	if (error != SUCCESS)
	{
		free(data.res);
		fclose(inputFile);
		fprintf(stderr, "error after reading IDAT");
		return error;
	}
	fclose(inputFile);
	f_uncompress(&head, &data);
	free(data.res);

	if (data.out == NULL)
	{
		fprintf(stderr, "error after uncompress data");
		return error;
	}
	if (plte.colourSize == 0)
	{
		filter(&data, &head);
	}

	if (error != SUCCESS)
	{
		fprintf(stderr, "wrong data in filter");
		free(data.out);
		return error;
	}

	FILE *outFile = fopen(argv[2], "wb");

	if (outFile == NULL)
	{
		fprintf(stderr, "error with out file");
		free(data.out);
		return ERROR_CANNOT_OPEN_FILE;
	}

	convertResult(&head, outFile, &data, &plte);
	fclose(outFile);
	free(data.out);
	return SUCCESS;
}
