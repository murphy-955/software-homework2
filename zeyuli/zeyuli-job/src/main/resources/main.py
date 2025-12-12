#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
SSE 流式返回 DeepSeek 旅行计划 JSON
java 调用方式示例：
  java 通过 ProcessBuilder 启动本脚本，把参数 JSON 写入 stdin，
  然后逐行读取 stdout 即可获得 SSE 片段，与原有 Flux<String> 兼容。
"""
import json
import sys
import os
from datetime import datetime
from openai import OpenAI
# 在 main.py 中添加，放在文件开头
import sys
import io

# 设置无缓冲输出
sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding='utf-8', write_through=True)


# ---------- 配置 ----------
client = OpenAI(
    api_key=os.getenv("DEEPSeek_API_KEY", "sk-84dbc668f2d048ddb6c39ace5ae78f47"),
    base_url="https://api.deepseek.com"
)
MODEL = "deepseek-chat"


# ---------- 工具 ----------
def build_prompt(data: dict) -> str:
    """
    完全复刻 Java 中的 buildJsonTravelPrompt / buildJsonTravelPromptWithUserInput
    逻辑，保证输出格式一致。
    """
    start = data["startCity"]
    end = data["endCity"]
    start_date = datetime.fromisoformat(data["startDate"])
    end_date = datetime.fromisoformat(data["endDate"])
    user_input = data.get("userInput", "").strip()

    days = (end_date - start_date).days + 1
    fmt_date = lambda d: d.strftime("%Y-%m-%d")

    if not user_input:
        # 无用户输入版本
        return f"""
你是严格的 JSON 生成器，只返回合法 JSON，禁止任何注释、解释、markdown 代码块。
若格式错误，用户将无法解析，视为严重事故。

下面是一段已验证的范例，你必须保持完全相同的键名、嵌套深度、数据类型（String/Number/List），仅把内容替换成本次行程的真实信息。

=== 范例开始 ===
{{
  "days": [
    {{
      "dayIndex": 1,
      "date": "2025-12-20",
      "label": "第1天",
      "items": [
        {{
          "time": "09:00",
          "title": "抵达厦门",
          "description": "- 抵达 **厦门高崎机场**\\\\n- 乘坐 **机场快线** 前往市区\\\\n> 建议提前购买 **厦门公交卡**",
          "attractions": "厦门高崎机场",
          "cost": "约30元",
          "durationHours": 1
        }}
      ]
    }}
  ]
}}
=== 范例结束 ===

本次行程信息：
- 出发地：{start}
- 目的地：{end}
- 出发日期：{fmt_date(start_date)}
- 返程日期：{fmt_date(end_date)}
- 总天数：{days}

严格按照范例的键名、结构、类型生成 JSON，description 字段允许使用 Markdown 排版。
禁止输出范例之外的多余文字、禁止包裹 ```json、禁止注释。
""".strip()
    else:
        # 有用户输入版本
        return f"""
你是资深旅行规划师。用户已对行程提出修改要求，请基于要求调整并重新输出完整 JSON。
输出格式与字段要求与刚才完全相同，description 仍支持 Markdown。

用户修改要求：
{user_input}

行程天数：{days} 天
出发：{start} → {end}，{fmt_date(start_date)} 至 {fmt_date(end_date)}

请直接返回 JSON，不要任何额外文字。
""".strip()


def stream_json_travel(data: dict):
    """调用 DeepSeek 并逐 chunk yield 文本"""
    prompt = build_prompt(data)  # 明确构建 prompt
    schema = {
        "type": "object",
        "properties": {
            "days": {
                "type": "array",
                "items": {
                    "type": "object",
                    "properties": {
                        "dayIndex": {"type": "string"},
                        "date": {"type": "string"},
                        "label": {"type": "string"},
                        "items": {
                            "type": "array",
                            "items": {
                                "type": "object",
                                "properties": {
                                    "time": {"type": "string"},
                                    "title": {"type": "string"},
                                    "description": {"type": "string"},
                                    "attractions": {"type": "string"},
                                    "cost": {"type": "string"},
                                    "durationHours": {"type": "string"},
                                },
                                "required": ["time", "title", "description", "attractions", "cost", "durationHours"]
                            }
                        }
                    },
                    "required": ["dayIndex", "date", "label", "items"]
                }
            }
        },
        "required": ["days"]
    }

    # 调用模型接口时使用 prompt 变量
    response = client.chat.completions.create(
        model=MODEL,
        messages=[
            {"role": "user", "content": prompt}  # 使用已定义的 prompt
        ],
        response_format={"type": "json_object"},  # 移除了无效的 json_schema 参数
        stream=True
    )

    for chunk in response:
        delta = chunk.choices[0].delta.content
        if delta:
            yield delta


# ---------- main ----------
if __name__ == "__main__":
    # 1. 读参数
    raw = sys.stdin.read().strip()
    if not raw:
        print("error: empty stdin", flush=True)
        sys.exit(1)
    try:
        args = json.loads(raw)
    except Exception as e:
        print(f"error: invalid json - {e}", flush=True)
        sys.exit(1)

    # 2. 逐片输出，模拟 SSE（每行一段）
    try:
        for piece in stream_json_travel(args):
            print(piece, flush=True)
    except Exception as e:
        print(f"error: {e}", flush=True)
        sys.exit(1)
