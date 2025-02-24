import HW4.T2

data Expr
    = Val Double
    | Add Expr Expr
    | Sub Expr Expr
    | Mul Expr Expr
    | Div Expr Expr
    deriving (Show, Eq)

main :: IO ()
main = do
    print $ parseExpr "24 - 1"        -- Ожидается: Sub (Val 24) (Val 1)
    print $ parseExpr "24 - 1 + 5"    -- Ожидается: Add (Sub (Val 24) (Val 1)) (Val 5)
    print $ parseExpr "(24 - 1) + 5"  -- Ожидается: Add (Sub (Val 24) (Val 1)) (Val 5)
    print $ parseExpr "3 * 4 + 5"     -- Ожидается: Add (Mul (Val 3) (Val 4)) (Val 5)
    print $ parseExpr "3 + 4 * 5"     -- Ожидается: Add (Val 3) (Mul (Val 4) (Val 5))
    print $ parseExpr "10 / 2 - 3"    -- Ожидается: Sub (Div (Val 10) (Val 2)) (Val 3)
    print $ parseExpr "1 - 1"
    print $ parseExpr "24978.626412468522"
