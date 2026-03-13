import styled from 'styled-components'

export const Page = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  gap: 16px;
`

export const Form = styled.form`
  display: flex;
  flex-direction: column;
  gap: 10px;
  width: 280px;
`

export const Input = styled.input`
  padding: 8px 10px;
  font-size: 14px;
`

export const Button = styled.button`
  padding: 8px 14px;
  cursor: pointer;
  font-size: 14px;

  &:disabled {
    opacity: 0.5;
    cursor: default;
  }
`
