#include <iostream>
#include <vector>
#include "algorithm"
using namespace std;

vector<vector<int>> gr;
vector<int> used;
vector<int> res;
bool flag;

void dfs(int v){
    used[v] = 1;
    for (int i : gr[v]) {
        if (used[i] == 0){
            dfs(i);
        } else if (used[i] == 1){
            flag = true;
        }
        if (flag) return;
    }
    used[v] = 2;
    res.push_back(v + 1);
}
int main(){
    int n, m;
    cin >> n >> m;
    used.resize(n + 1);
    gr.resize(n + 1, vector<int>());
    for (int i = 0; i < m; i++){
        int a, b;
        cin >> a >> b;
        gr[a-1].push_back(b-1);
    }
    for (int i = 0; i < n; i++){
        if (used[i] == 0){
            dfs(i);
        }
        if (flag){
            break;
        }
    }

    reverse(res.begin(), res.end());
    if (flag) {
        cout << -1;
    } else {
        for (int a: res) {
            cout << a << " ";
        }
    }
}
 