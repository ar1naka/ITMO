#include "LN.h"
#include "return_codes.h"

#include <fstream>
#include <string>
#include <vector>

std::pair< LN, LN > read(std::vector< LN > &stack)
{
	LN r = stack.back();
	stack.pop_back();
	LN l = stack.back();
	stack.pop_back();
	return { l, r };
}

int main(int argc, char *argv[])
{
	if (argc != 3)
	{
		fprintf(stderr, "Not enough arguments");
		return ERROR_PARAMETER_INVALID;
	}
	std::ifstream inputFile(argv[1]);
	if (!inputFile.is_open())
	{
		fprintf(stderr, "wrong input data");
		return ERROR_CANNOT_OPEN_FILE;
	}
	std::string str;
	std::vector< LN > stack;
	while (inputFile >> str)
	{
		try
		{
			if (str == "+")
			{
				auto a = read(stack);
				stack.push_back(a.second + a.first);
			}
			else if (str == "-")
			{
				auto a = read(stack);
				stack.push_back(a.second - a.first);
			}
			else if (str == "*")
			{
				auto a = read(stack);
				stack.push_back(a.second * a.first);
			}
			else if (str == "/")
			{
				auto a = read(stack);
				stack.push_back(a.second / a.first);
			}
			else if (str == "%")
			{
				auto a = read(stack);
				stack.push_back(a.second % a.first);
			}
			else if (str == "+=")
			{
				auto a = read(stack);
				stack.push_back(a.second += a.first);
			}
			else if (str == "-=")
			{
				auto a = read(stack);
				stack.push_back(a.second -= a.first);
			}
			else if (str == "*=")
			{
				auto a = read(stack);
				stack.push_back(a.second *= a.first);
			}
			else if (str == "/=")
			{
				auto a = read(stack);
				stack.push_back(a.second += a.first);
			}
			else if (str == "%=")
			{
				auto a = read(stack);
				stack.push_back(a.second %= a.first);
			}
			else if (str == "<=")
			{
				auto a = read(stack);
				stack.emplace_back(a.second <= a.first);
			}
			else if (str == "==")
			{
				auto a = read(stack);
				stack.emplace_back(a.second == a.first);
			}
			else if (str == "<")
			{
				auto a = read(stack);
				stack.emplace_back(a.second < a.first);
			}
			else if (str == ">")
			{
				auto a = read(stack);
				stack.emplace_back(a.second > a.first);
			}
			else if (str == ">=")
			{
				auto a = read(stack);
				stack.emplace_back(a.second >= a.first);
			}
			else if (str == "!=")
			{
				auto a = read(stack);
				stack.emplace_back(a.second != a.first);
			}
			else if (str == "~")
			{
				LN r = stack.back();
				stack.pop_back();
				stack.push_back(~r);
			}
			else if (str == "_")
			{
				LN r = stack.back();
				stack.pop_back();
				stack.push_back(-r);
			}
			else
			{
				stack.emplace_back(str);
			}
		} catch (std::runtime_error &error)
		{
			inputFile.close();
			fprintf(stderr, "%s", error.what());
			return ERROR_OUT_OF_MEMORY;
		}
	}
	inputFile.close();

	std::ofstream output(argv[2]);
	if (!output.is_open())
	{
		fprintf(stderr, "error with out file");
		return ERROR_CANNOT_OPEN_FILE;
	}
	while (!stack.empty())
	{
		LN a = stack.back();
		a.convert(output);
		output << "\n";
		stack.pop_back();
	}
	output.close();
	return SUCCESS;
}
